package io.reflectoring.buckpal.account.application.port.`in`

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.common.SelfValidating

data class SendMoneyCommand(
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val money: Money
) : SelfValidating<SendMoneyCommand?>() {
    init {
        validateSelf()
    }
}