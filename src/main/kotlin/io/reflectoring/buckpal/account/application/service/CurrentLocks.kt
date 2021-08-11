package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.domain.Account

class CurrentLocks(private val accountLock: AccountLock) {
    private val locked: MutableList<Account> = mutableListOf()

    fun add(accountToLock: Account) {
        locked.add(accountToLock)
        accountLock.lockAccount(accountToLock.id)
    }

    fun release() {
        locked.forEach { accountLock.releaseAccount(it.id) }
    }
}
