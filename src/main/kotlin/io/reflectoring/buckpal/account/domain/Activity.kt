package io.reflectoring.buckpal.account.domain

import io.reflectoring.buckpal.account.domain.Account.AccountId
import java.time.LocalDateTime

/**
 * A money transfer activity between [Account]s.
 */
data class Activity(
    val id: ActivityId?,
    val ownerAccountId: AccountId,
    val sourceAccountId: AccountId,
    val targetAccountId: AccountId,
    val timestamp: LocalDateTime,
    val money: Money
) {
    data class ActivityId(val value: Long)
}