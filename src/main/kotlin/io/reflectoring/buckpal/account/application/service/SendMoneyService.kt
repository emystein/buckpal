package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.LoadAccountPort
import io.reflectoring.buckpal.account.application.port.out.UpdateAccountStatePort
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
    override fun sendMoney(command: SendMoneyCommand) {
        checkThreshold(command)

        val baselineDate = LocalDateTime.now().minusDays(10)
        val sourceAccount = loadAccountPort.loadAccount(command.sourceAccountId, baselineDate)
        val targetAccount = loadAccountPort.loadAccount(command.targetAccountId, baselineDate)

        accountLock.lockAccount(sourceAccount.id)

        try {
            sourceAccount.withdraw(command.money, targetAccount.id)
        } catch (exception : Exception) {
            accountLock.releaseAccount(sourceAccount.id)
            return
        }

        accountLock.lockAccount(targetAccount.id)

        try {
            targetAccount.deposit(command.money, sourceAccount.id)
        } catch (exception : Exception) {
            accountLock.releaseAccount(sourceAccount.id)
            accountLock.releaseAccount(targetAccount.id)
            return
        }

        updateAccountStatePort.updateActivities(sourceAccount)
        updateAccountStatePort.updateActivities(targetAccount)

        accountLock.releaseAccount(sourceAccount.id)
        accountLock.releaseAccount(targetAccount.id)
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