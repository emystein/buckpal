package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.common.UseCase
import java.time.LocalDateTime
import javax.transaction.Transactional

@UseCase
@Transactional
class SendMoneyService(
    private val accountRepository: AccountRepository,
    private val accountLock: AccountLock,
    private val moneyTransferProperties: MoneyTransferProperties
) : SendMoneyUseCase {
    override fun sendMoney(command: SendMoneyCommand) {
        checkThreshold(command)

        val baselineDate = LocalDateTime.now().minusDays(10)

        val sourceAccount = accountRepository.loadAccount(command.sourceAccountId, baselineDate)
        val targetAccount = accountRepository.loadAccount(command.targetAccountId, baselineDate)

        val locks = CurrentLocks(accountLock)

        try {
            locks.add(sourceAccount.id)
            sourceAccount.withdraw(command.money, targetAccount.id)

            locks.add(targetAccount.id)
            targetAccount.deposit(command.money, sourceAccount.id)

            accountRepository.updateActivities(sourceAccount)
            accountRepository.updateActivities(targetAccount)
        } catch (exception : Exception) {
            return
        } finally {
            locks.release()
        }
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