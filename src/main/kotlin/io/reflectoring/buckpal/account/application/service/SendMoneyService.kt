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
        checkThreshold(command)

        val (sourceAccount, targetAccount) = loadAccounts(command)

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

    private fun checkThreshold(command: SendMoneyCommand) {
        command.checkThreshold(moneyTransferThreshold())
    }

    private fun moneyTransferThreshold() = moneyTransferProperties.maximumTransferThreshold

    private fun loadAccounts(command: SendMoneyCommand): SourceTargetAccounts {
        val loader = SendMoneyLoader(accountRepository, moneyTransferProperties)
        return loader.loadAccounts(command)
    }

    private fun updateActivities(vararg accounts: Account) {
        accounts.forEach { account -> accountRepository.updateActivities(account) }
    }
}