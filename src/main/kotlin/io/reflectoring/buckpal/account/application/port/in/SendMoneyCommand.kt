package io.reflectoring.buckpal.account.application.port.`in`

import io.reflectoring.buckpal.account.application.service.ThresholdExceededException
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

    fun checkThreshold(maximumTransferThreshold: Money) {
        if (money.isGreaterThan(maximumTransferThreshold)) {
            throw ThresholdExceededException(maximumTransferThreshold, money)
        }
    }
}