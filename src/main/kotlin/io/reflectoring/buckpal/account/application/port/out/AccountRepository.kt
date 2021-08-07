package io.reflectoring.buckpal.account.application.port.out

import io.reflectoring.buckpal.account.domain.Account
import java.time.LocalDateTime

interface AccountRepository {
    fun loadAccount(accountId: Account.AccountId, baselineDate: LocalDateTime): Account
    fun updateActivities(account: Account)
}
