package io.reflectoring.buckpal.account.application.port.out

import io.reflectoring.buckpal.account.domain.Account.AccountId
import java.time.LocalDateTime
import io.reflectoring.buckpal.account.domain.Account

interface LoadAccountPort {
    fun loadAccount(accountId: AccountId, baselineDate: LocalDateTime): Account
}