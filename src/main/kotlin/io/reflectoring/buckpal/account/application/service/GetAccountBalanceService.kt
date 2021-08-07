package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.`in`.GetAccountBalanceQuery
import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Money
import java.time.LocalDateTime

class GetAccountBalanceService(private val accounts: AccountRepository) : GetAccountBalanceQuery {
    override fun getAccountBalance(accountId: Account.AccountId): Money {
        return accounts.loadAccount(accountId, LocalDateTime.now()).calculateBalance()
    }
}