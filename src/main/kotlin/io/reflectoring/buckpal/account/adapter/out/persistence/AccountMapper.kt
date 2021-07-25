package io.reflectoring.buckpal.account.adapter.out.persistence

import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Account.Companion.withId
import io.reflectoring.buckpal.account.domain.Activity
import io.reflectoring.buckpal.account.domain.Activity.ActivityId
import io.reflectoring.buckpal.account.domain.ActivityWindow
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import io.reflectoring.buckpal.account.domain.Money.Companion.subtract
import org.springframework.stereotype.Component

@Component
class AccountMapper {
    fun mapToDomainEntity(
        account: AccountJpaEntity,
        activities: List<ActivityJpaEntity>,
        withdrawalBalance: Long,
        depositBalance: Long
    ): Account {
        val baselineBalance = subtract(
            of(depositBalance),
            of(withdrawalBalance)
        )
        return withId(
            AccountId(account.id),
            baselineBalance,
            mapToActivityWindow(activities)
        )
    }

    fun mapToActivityWindow(activities: List<ActivityJpaEntity>): ActivityWindow {
        val mappedActivities: MutableList<Activity> = ArrayList()
        for (activity in activities) {
            mappedActivities.add(
                Activity(
                    ActivityId(activity.id),
                    AccountId(activity.ownerAccountId),
                    AccountId(activity.sourceAccountId),
                    AccountId(activity.targetAccountId),
                    activity.timestamp,
                    of(activity.amount)
                )
            )
        }
        return ActivityWindow(mappedActivities)
    }

    fun mapToJpaEntity(activity: Activity): ActivityJpaEntity {
        return ActivityJpaEntity(
            activity.id!!.value,
            activity.timestamp,
            activity.ownerAccountId.value,
            activity.sourceAccountId.value,
            activity.targetAccountId.value,
            activity.money.amount.toLong()
        )
    }
}