package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.domain.Account

class CurrentLocks(private val accountLock: AccountLock) {
    private val locked: MutableList<Account.AccountId> = mutableListOf()

    fun add(id: Account.AccountId) {
        locked.add(id)
        accountLock.lockAccount(id)
    }

    fun release() {
        locked.forEach { accountLock.releaseAccount(it) }
    }
}
