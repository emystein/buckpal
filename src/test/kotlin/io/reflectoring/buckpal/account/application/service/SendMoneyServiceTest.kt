package io.reflectoring.buckpal.account.application.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.LoadAccountPort
import io.reflectoring.buckpal.account.application.port.out.UpdateAccountStatePort
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Money
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
        val sourceAccountId = Account.AccountId(41L)
        val sourceAccount = givenAnAccountWithId(sourceAccountId)
        val targetAccountId = Account.AccountId(42L)
        val targetAccount = givenAnAccountWithId(targetAccountId)
        givenWithdrawalWillFail(sourceAccount)
        givenDepositWillSucceed(targetAccount)
        val command = SendMoneyCommand(
            sourceAccountId,
            targetAccountId,
            Money.of(300L)
        )

        justRun { accountLock.lockAccount(sourceAccountId) }
        justRun { accountLock.releaseAccount(sourceAccountId) }

        val success = sendMoneyService.sendMoney(command)
        assertThat(success).isFalse

        verify(exactly = 0) { accountLock.lockAccount(targetAccountId) }
    }

    @Test
    fun transactionSucceeds() {
        val sourceAccount: Account = givenSourceAccount()
        val targetAccount: Account = givenTargetAccount()
        givenWithdrawalWillSucceed(sourceAccount)
        givenDepositWillSucceed(targetAccount)
        val money: Money = Money.of(500L)
        val command = SendMoneyCommand(
            sourceAccount.id,
            targetAccount.id,
            money
        )

        val sourceAccountId: Account.AccountId = sourceAccount.id
        val targetAccountId: Account.AccountId = targetAccount.id

        justRun { accountLock.lockAccount(sourceAccountId) }
        every { sourceAccount.withdraw(money, targetAccountId) } returns true
        justRun { accountLock.releaseAccount(sourceAccountId) }
        justRun { accountLock.lockAccount(targetAccountId) }
        every { targetAccount.deposit(money, sourceAccountId) } returns true
        justRun { accountLock.releaseAccount(targetAccountId) }

        justRun { updateAccountStatePort.updateActivities(sourceAccount) }
        justRun { updateAccountStatePort.updateActivities(targetAccount) }

        val success = sendMoneyService.sendMoney(command)

        assertThat(success).isTrue
    }

    private fun givenDepositWillSucceed(account: Account) {
        every { account.deposit(any(), any()) } returns true
    }

    private fun givenWithdrawalWillFail(account: Account) {
        every { account.withdraw(any(), any()) } returns false
    }

    private fun givenWithdrawalWillSucceed(account: Account) {
        every { account.withdraw(any(), any()) } returns true
    }

    private fun givenTargetAccount(): Account {
        return givenAnAccountWithId(Account.AccountId(42L))
    }

    private fun givenSourceAccount(): Account {
        return givenAnAccountWithId(Account.AccountId(41L))
    }

    private fun givenAnAccountWithId(id: Account.AccountId): Account {
        val account = mockk<Account>()

        every { account.id } returns id

        every { loadAccountPort.loadAccount(id, any()) } returns account

        return account
    }

    private fun moneyTransferProperties(): MoneyTransferProperties {
        return MoneyTransferProperties(Money.of(Long.MAX_VALUE))
    }
}