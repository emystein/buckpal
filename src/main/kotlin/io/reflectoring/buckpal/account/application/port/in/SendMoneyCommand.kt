package io.reflectoring.buckpal.account.application.port.`in`

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Money

data class SendMoneyCommand(
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val money: Money
) {
    init {
        checkAmountGreaterThan0()
    }


    private fun checkAmountGreaterThan0() {
        if (money.isNotPositive) {
            throw NotPositiveAmountException(money)
        }
    }
}