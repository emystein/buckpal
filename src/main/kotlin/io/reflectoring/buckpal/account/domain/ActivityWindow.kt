package io.reflectoring.buckpal.account.domain

import io.reflectoring.buckpal.account.domain.Account.AccountId
import java.time.LocalDateTime
import java.lang.IllegalStateException
import java.util.*

/**
 * A window of account activities.
 */
class ActivityWindow {
    /**
     * The list of account activities within this window.
     */
    private var activities: MutableList<Activity>

    /**
     * The timestamp of the first activity within this window.
     */
    val startTimestamp: LocalDateTime
        get() = activities.stream()
            .min(Comparator.comparing { obj: Activity -> obj.timestamp })
            .orElseThrow { IllegalStateException() }
            .timestamp

    /**
     * The timestamp of the last activity within this window.
     * @return
     */
    val endTimestamp: LocalDateTime
        get() = activities.stream()
            .max(Comparator.comparing { obj: Activity -> obj.timestamp })
            .orElseThrow { IllegalStateException() }
            .timestamp

    /**
     * Calculates the balance by summing up the values of all activities within this window.
     */
    fun calculateBalance(accountId: AccountId): Money {
        val depositBalance = activities.stream()
            .filter { a: Activity -> a.targetAccountId == accountId }
            .map { obj: Activity -> obj.money }
            .reduce(Money.Companion.ZERO) { a: Money, b: Money? -> Money.Companion.add(a, b) }
        val withdrawalBalance = activities.stream()
            .filter { a: Activity -> a.sourceAccountId == accountId }
            .map { obj: Activity -> obj.money }
            .reduce(Money.Companion.ZERO) { a: Money, b: Money? -> Money.Companion.add(a, b) }
        return Money.Companion.add(depositBalance, withdrawalBalance.negate())
    }

    constructor(activities: MutableList<Activity>) {
        this.activities = activities
    }

    constructor(vararg activities: Activity?) {
        this.activities = ArrayList(Arrays.asList(*activities))
    }

    fun getActivities(): List<Activity> {
        return Collections.unmodifiableList(activities)
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }
}