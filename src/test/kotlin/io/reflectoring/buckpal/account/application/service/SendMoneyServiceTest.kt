package io.reflectoring.buckpal.account.application.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.LoadAccountPort
import io.reflectoring.buckpal.account.application.port.out.UpdateAccountStatePort
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.ActivityWindow
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.account.domain.Money.Companion.ZERO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SendMoneyServiceTest {
    @MockK
    lateinit var loadAccountPort: LoadAccountPort

    @MockK
    lateinit var accountLock: AccountLock

    @MockK
    lateinit var updateAccountStatePort: UpdateAccountStatePort

    lateinit var sendMoneyService: SendMoneyService

    @BeforeEach
    internal fun setUp() {
        sendMoneyService =
            SendMoneyService(loadAccountPort, accountLock, updateAccountStatePort, moneyTransferProperties())
    }

    @Test
    fun givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        val sourceAccount = givenSourceAccount(balance = ZERO)
        val targetAccount = givenTargetAccount(balance = ZERO)
        
        val command = SendMoneyCommand(sourceAccount.id, targetAccount.id, Money.of(300L))

        expectLockThenRelease(sourceAccount)

        sendMoneyService.sendMoney(command)

        verify(exactly = 0) { accountLock.releaseAccount(targetAccount.id) }
    }

    @Test
    fun transactionSucceeds() {
        val sourceAccount = givenSourceAccount(balance = Money.of(1000L))
        val targetAccount = givenTargetAccount(balance = ZERO)

        val command = SendMoneyCommand(sourceAccount.id, targetAccount.id, Money.of(500L))

        expectLockThenRelease(sourceAccount)
        expectLockThenRelease(targetAccount)

        justRun { updateAccountStatePort.updateActivities(sourceAccount) }
        justRun { updateAccountStatePort.updateActivities(targetAccount) }

        sendMoneyService.sendMoney(command)
    }

    private fun expectLockThenRelease(sourceAccount: Account) {
        justRun { accountLock.lockAccount(sourceAccount.id) }
        justRun { accountLock.releaseAccount(sourceAccount.id) }
    }

    private fun givenSourceAccount(balance: Money): Account {
        return givenAnAccountWithId(AccountId(41L), balance)
    }

    private fun givenTargetAccount(balance: Money): Account {
        return givenAnAccountWithId(AccountId(42L), balance)
    }

    private fun givenAnAccountWithId(id: AccountId, balance: Money): Account {
        val account = Account(id, balance, ActivityWindow())

        every { loadAccountPort.loadAccount(id, any()) } returns account

        return account
    }

    private fun moneyTransferProperties(): MoneyTransferProperties {
        return MoneyTransferProperties(Money.of(Long.MAX_VALUE))
    }
}