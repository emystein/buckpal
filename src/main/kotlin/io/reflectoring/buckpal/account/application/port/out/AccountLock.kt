package io.reflectoring.buckpal.account.application.port.out

import io.reflectoring.buckpal.account.domain.Account.AccountId

interface AccountLock {
    fun lockAccount(accountId: AccountId)
    fun releaseAccount(accountId: AccountId)
}