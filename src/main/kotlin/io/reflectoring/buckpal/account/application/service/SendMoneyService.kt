package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.LoadAccountPort
import io.reflectoring.buckpal.account.application.port.out.UpdateAccountStatePort
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.common.UseCase
import java.time.LocalDateTime
import javax.transaction.Transactional

@UseCase
@Transactional
class SendMoneyService(
    private val loadAccountPort: LoadAccountPort,
    private val accountLock: AccountLock,
    private val updateAccountStatePort: UpdateAccountStatePort,
    private val moneyTransferProperties: MoneyTransferProperties
) : SendMoneyUseCase {
    override fun sendMoney(command: SendMoneyCommand): Boolean {
        checkThreshold(command)

        val baselineDate: LocalDateTime = LocalDateTime.now().minusDays(10)
        val sourceAccount: Account = loadAccountPort.loadAccount(command.sourceAccountId, baselineDate)
        val targetAccount: Account = loadAccountPort.loadAccount(command.targetAccountId, baselineDate)

        accountLock.lockAccount(sourceAccount.id)
        if (!sourceAccount.withdraw(command.money, targetAccount.id)) {
            accountLock.releaseAccount(sourceAccount.id)
            return false
        }

        accountLock.lockAccount(targetAccount.id)
        if (!targetAccount.deposit(command.money, sourceAccount.id)) {
            accountLock.releaseAccount(sourceAccount.id)
            accountLock.releaseAccount(targetAccount.id)
            return false
        }

        updateAccountStatePort.updateActivities(sourceAccount)
        updateAccountStatePort.updateActivities(targetAccount)
        accountLock.releaseAccount(sourceAccount.id)
        accountLock.releaseAccount(targetAccount.id)

        return true
    }

    private fun checkThreshold(command: SendMoneyCommand) {
        if (command.money.isGreaterThan(moneyTransferProperties.maximumTransferThreshold)) {
            throw ThresholdExceededException(
                moneyTransferProperties.maximumTransferThreshold,
                command.money
            )
        }
    }
}