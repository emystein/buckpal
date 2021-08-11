package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.common.UseCase
import javax.transaction.Transactional

@UseCase
@Transactional
class SendMoneyService(
    private val accountRepository: AccountRepository,
    private val accountLock: AccountLock,
    private val moneyTransferProperties: MoneyTransferProperties
) : SendMoneyUseCase {
    override fun sendMoney(command: SendMoneyCommand) {
        command.checkThreshold(moneyTransferThreshold())

        val sourceAccount = loadAccount(command.sourceAccountId)
        val targetAccount = loadAccount(command.targetAccountId)

        val locks = CurrentLocks(accountLock)

        try {
            locks.add(sourceAccount)
            sourceAccount.withdraw(command.money, targetAccount.id)

            locks.add(targetAccount)
            targetAccount.deposit(command.money, sourceAccount.id)

            updateActivities(sourceAccount, targetAccount)
        } finally {
            locks.release()
        }
    }

    private fun moneyTransferThreshold() = moneyTransferProperties.maximumTransferThreshold

    private fun loadAccount(accountId: Account.AccountId): Account {
        return accountRepository.loadAccount(accountId, baselineDateFromNow())
    }

    private fun baselineDateFromNow() = moneyTransferProperties.baseLineDateFromNow()

    private fun updateActivities(vararg accounts: Account) {
        accounts.forEach { account -> accountRepository.updateActivities(account) }
    }
}