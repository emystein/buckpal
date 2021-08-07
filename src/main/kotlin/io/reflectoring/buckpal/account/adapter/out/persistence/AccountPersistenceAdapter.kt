package io.reflectoring.buckpal.account.adapter.out.persistence

import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Activity
import io.reflectoring.buckpal.common.PersistenceAdapter
import java.time.LocalDateTime
import javax.persistence.EntityNotFoundException

@PersistenceAdapter
internal class AccountPersistenceAdapter(
    private val accountRepository: SpringDataAccountRepository,
    private val activityRepository: ActivityRepository,
    private val accountMapper: AccountMapper
) : AccountRepository {
    override fun loadAccount(
        accountId: AccountId,
        baselineDate: LocalDateTime
    ): Account {
        val account = accountRepository.findById(accountId.value)
            .orElseThrow { EntityNotFoundException() }
        val activities = activityRepository.findByOwnerSince(
            accountId.value,
            baselineDate
        )
        val withdrawalBalance = orZero(
            activityRepository
                .getWithdrawalBalanceUntil(
                    accountId.value,
                    baselineDate
                )
        )
        val depositBalance = orZero(
            activityRepository
                .getDepositBalanceUntil(
                    accountId.value,
                    baselineDate
                )
        )
        return accountMapper.mapToDomainEntity(
            account!!,
            activities,
            withdrawalBalance,
            depositBalance
        )
    }

    private fun orZero(value: Long?): Long {
        return value ?: 0L
    }

    override fun updateActivities(account: Account) {
        for (activity in account.activityWindow.getActivities()) {
            if (activity.id == null || activity.id == Activity.ActivityId(0)) {
                activityRepository.save(accountMapper.mapToJpaEntity(activity))
            }
        }
    }
}