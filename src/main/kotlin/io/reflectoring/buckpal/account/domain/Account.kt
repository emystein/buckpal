package io.reflectoring.buckpal.account.domain

import java.time.LocalDateTime

/**
 * An account that holds a certain amount of money. An [Account] object only
 * contains a window of the latest account activities. The total balance of the account is
 * the sum of a baseline balance that was valid before the first activity in the
 * window and the sum of the activity values.
 */
class Account(val id: AccountId, val baselineBalance: Money, val activityWindow: ActivityWindow) {
    /**
     * Calculates the total balance of the account by adding the activity values to the baseline balance.
     */
    fun calculateBalance(): Money {
        return Money.add(
            baselineBalance,
            activityWindow.calculateBalance(id)
        )
    }

    /**
     * Tries to withdraw a certain amount of money from this account.
     * If successful, creates a new activity with a negative value.
     */
    fun withdraw(money: Money, targetAccountId: AccountId) {
        if (!mayWithdraw(money)) {
            throw RuntimeException("Account ${this.id} cannot withdraw $money")
        }

        val withdrawal = Activity(
            Activity.ActivityId(0),
            id,
            id,
            targetAccountId,
            LocalDateTime.now(),
            money
        )

        activityWindow.addActivity(withdrawal)
    }

    private fun mayWithdraw(money: Money): Boolean {
        return Money.add(calculateBalance(), money.negate()).isPositiveOrZero
    }

    /**
     * Tries to deposit a certain amount of money to this account.
     * If sucessful, creates a new activity with a positive value.
     */
    fun deposit(money: Money, sourceAccountId: AccountId) {
        val deposit = Activity(Activity.ActivityId(0), id, sourceAccountId, id, LocalDateTime.now(), money)

        activityWindow.addActivity(deposit)
    }

    data class AccountId(val value: Long)

    companion object {
        /**
         * Creates an [Account] entity without an ID. Use to create a new entity that is not yet
         * persisted.
         */
        fun withoutId(
            baselineBalance: Money,
            activityWindow: ActivityWindow
        ): Account {
            return Account(AccountId(0), baselineBalance, activityWindow)
        }

        /**
         * Creates an [Account] entity with an ID. Use to reconstitute a persisted entity.
         */
		fun withId(
            accountId: AccountId,
            baselineBalance: Money,
            activityWindow: ActivityWindow
        ): Account {
            return Account(accountId, baselineBalance, activityWindow)
        }
    }
}