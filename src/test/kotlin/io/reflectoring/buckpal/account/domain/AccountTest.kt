package io.reflectoring.buckpal.account.domain

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.common.AccountTestData
import io.reflectoring.buckpal.common.ActivityTestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class AccountTest {
    @Test
    fun calculatesBalance() {
        val accountId = AccountId(1L)
        val account = AccountTestData.defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(555L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(999L)).build(),
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(1L)).build()
                )
            )
            .build()

        val balance = account.calculateBalance()

        Assertions.assertThat(balance).isEqualTo(Money.of(1555L))
    }

    @Test
    fun withdrawalSucceeds() {
        val accountId = AccountId(1L)

        val account = AccountTestData.defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(555L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(999L)).build(),
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(1L)).build()
                )
            )
            .build()

        account.withdraw(Money.of(555L), AccountId(99L))

        Assertions.assertThat(account.activityWindow.getActivities()).hasSize(3)
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(1000L))
    }

    @Test
    fun withdrawalFailure() {
        val accountId = AccountId(1L)

        val account = AccountTestData.defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(555L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(999L)).build(),
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(1L)).build()
                )
            )
            .build()

        assertThrows(Exception::class.java) { account.withdraw(Money.of(1556L), AccountId(99L)) }

        Assertions.assertThat(account.activityWindow.getActivities()).hasSize(2)
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(1555L))
    }

    @Test
    fun depositSuccess() {
        val accountId = AccountId(1L)
        val account = AccountTestData.defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(555L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(999L)).build(),
                    ActivityTestData.defaultActivity()
                        .withTargetAccount(accountId)
                        .withMoney(Money.of(1L)).build()
                )
            )
            .build()

        account.deposit(Money.of(445L), AccountId(99L))

        Assertions.assertThat(account.activityWindow.getActivities()).hasSize(3)
        Assertions.assertThat(account.calculateBalance()).isEqualTo(Money.of(2000L))
    }
}