package io.reflectoring.buckpal.common

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Activity
import io.reflectoring.buckpal.account.domain.Activity.ActivityId
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import java.time.LocalDateTime

object ActivityTestData {
    fun defaultActivity(): ActivityBuilder {
        return ActivityBuilder()
            .withOwnerAccount(AccountId(42L))
            .withSourceAccount(AccountId(42L))
            .withTargetAccount(AccountId(41L))
            .withTimestamp(LocalDateTime.now())
            .withMoney(of(999L))
    }

    class ActivityBuilder {
        private var id: ActivityId? = null
        private var ownerAccountId: AccountId? = null
        private var sourceAccountId: AccountId? = null
        private var targetAccountId: AccountId? = null
        private var timestamp: LocalDateTime? = null
        private var money: Money? = null
        fun withId(id: ActivityId?): ActivityBuilder {
            this.id = id
            return this
        }

        fun withOwnerAccount(accountId: AccountId?): ActivityBuilder {
            ownerAccountId = accountId
            return this
        }

        fun withSourceAccount(accountId: AccountId?): ActivityBuilder {
            sourceAccountId = accountId
            return this
        }

        fun withTargetAccount(accountId: AccountId?): ActivityBuilder {
            targetAccountId = accountId
            return this
        }

        fun withTimestamp(timestamp: LocalDateTime?): ActivityBuilder {
            this.timestamp = timestamp
            return this
        }

        fun withMoney(money: Money?): ActivityBuilder {
            this.money = money
            return this
        }

        fun build(): Activity {
            return Activity(
                id,
                ownerAccountId!!,
                sourceAccountId!!,
                targetAccountId!!,
                timestamp!!,
                money!!
            )
        }
    }
}