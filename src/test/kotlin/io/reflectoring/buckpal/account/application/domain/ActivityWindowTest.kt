package io.reflectoring.buckpal.account.domain

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import io.reflectoring.buckpal.common.ActivityTestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ActivityWindowTest {
    @Test
    fun calculatesStartTimestamp() {
        val window = ActivityWindow(
            ActivityTestData.defaultActivity().withTimestamp(startDate()).build(),
            ActivityTestData.defaultActivity().withTimestamp(inBetweenDate()).build(),
            ActivityTestData.defaultActivity().withTimestamp(endDate()).build()
        )
        Assertions.assertThat(window.startTimestamp).isEqualTo(startDate())
    }

    @Test
    fun calculatesEndTimestamp() {
        val window = ActivityWindow(
            ActivityTestData.defaultActivity().withTimestamp(startDate()).build(),
            ActivityTestData.defaultActivity().withTimestamp(inBetweenDate()).build(),
            ActivityTestData.defaultActivity().withTimestamp(endDate()).build()
        )
        Assertions.assertThat(window.endTimestamp).isEqualTo(endDate())
    }

    @Test
    fun calculatesBalance() {
        val account1 = AccountId(1L)
        val account2 = AccountId(2L)
        val window = ActivityWindow(
            ActivityTestData.defaultActivity()
                .withSourceAccount(account1)
                .withTargetAccount(account2)
                .withMoney(of(999)).build(),
            ActivityTestData.defaultActivity()
                .withSourceAccount(account1)
                .withTargetAccount(account2)
                .withMoney(of(1)).build(),
            ActivityTestData.defaultActivity()
                .withSourceAccount(account2)
                .withTargetAccount(account1)
                .withMoney(of(500)).build()
        )
        Assertions.assertThat(window.calculateBalance(account1)).isEqualTo(of(-500))
        Assertions.assertThat(window.calculateBalance(account2)).isEqualTo(of(500))
    }

    private fun startDate(): LocalDateTime {
        return LocalDateTime.of(2019, 8, 3, 0, 0)
    }

    private fun inBetweenDate(): LocalDateTime {
        return LocalDateTime.of(2019, 8, 4, 0, 0)
    }

    private fun endDate(): LocalDateTime {
        return LocalDateTime.of(2019, 8, 5, 0, 0)
    }
}