package io.reflectoring.buckpal.account.application.port.`in`

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Money

interface GetAccountBalanceQuery {
    fun getAccountBalance(accountId: AccountId): Money
}