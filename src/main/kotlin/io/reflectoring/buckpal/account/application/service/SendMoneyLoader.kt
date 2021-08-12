package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import java.time.LocalDateTime

class SendMoneyLoader(
    private val accountRepository: AccountRepository,
    private val baselineDateFromNow: LocalDateTime
) {
    fun loadAccounts(command: SendMoneyCommand): SourceTargetAccounts {
        val sourceAccount = loadAccount(command.sourceAccountId)
        val targetAccount = loadAccount(command.targetAccountId)
        return SourceTargetAccounts(sourceAccount, targetAccount)
    }

    private fun loadAccount(accountId: Account.AccountId): Account {
        return accountRepository.loadAccount(accountId, baselineDateFromNow)
    }
}