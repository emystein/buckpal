package io.reflectoring.buckpal.common

import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Account.Companion.withId
import io.reflectoring.buckpal.account.domain.ActivityWindow
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.account.domain.Money.Companion.of

object AccountTestData {
    fun defaultAccount(): AccountBuilder {
        return AccountBuilder()
            .withAccountId(AccountId(42L))
            .withBaselineBalance(of(999L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity().build(),
                    ActivityTestData.defaultActivity().build()
                )
            )
    }

    class AccountBuilder {
        private var accountId: AccountId? = null
        private var baselineBalance: Money? = null
        private var activityWindow: ActivityWindow? = null
        fun withAccountId(accountId: AccountId?): AccountBuilder {
            this.accountId = accountId
            return this
        }

        fun withBaselineBalance(baselineBalance: Money?): AccountBuilder {
            this.baselineBalance = baselineBalance
            return this
        }

        fun withActivityWindow(activityWindow: ActivityWindow?): AccountBuilder {
            this.activityWindow = activityWindow
            return this
        }

        fun build(): Account {
            return withId(accountId!!, baselineBalance!!, activityWindow!!)
        }
    }
}