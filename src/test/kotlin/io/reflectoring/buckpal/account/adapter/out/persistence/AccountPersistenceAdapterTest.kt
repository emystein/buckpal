package io.reflectoring.buckpal.account.adapter.out.persistence

import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Activity
import io.reflectoring.buckpal.account.domain.ActivityWindow
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import io.reflectoring.buckpal.common.AccountTestData
import io.reflectoring.buckpal.common.ActivityTestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

@DataJpaTest
@Import(AccountPersistenceAdapter::class, AccountMapper::class)
class AccountPersistenceAdapterTest {
    @Autowired
    private lateinit var adapterUnderTest: AccountPersistenceAdapter

    @Autowired
    private lateinit var activityRepository: ActivityRepository

    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    fun loadsAccount() {
        val account = adapterUnderTest.loadAccount(AccountId(1L), LocalDateTime.of(2018, 8, 10, 0, 0))
        Assertions.assertThat(account.activityWindow.getActivities()).hasSize(2)
        Assertions.assertThat(account.calculateBalance()).isEqualTo(of(500))
    }

    @Test
    fun updatesActivities() {
        val account = AccountTestData.defaultAccount()
            .withBaselineBalance(of(555L))
            .withActivityWindow(
                ActivityWindow(
                    ActivityTestData.defaultActivity()
                        .withId(Activity.ActivityId(0))
                        .withMoney(of(1L)).build()
                )
            )
            .build()
        adapterUnderTest.updateActivities(account)
        Assertions.assertThat(activityRepository.count()).isEqualTo(1)
        val (_, _, _, _, _, amount) = activityRepository.findAll()[0]
        Assertions.assertThat(amount).isEqualTo(1L)
    }
}