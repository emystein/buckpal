package io.reflectoring.buckpal.account.application.service

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.out.AccountLock
import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.ActivityWindow
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.account.domain.Money.Companion.ZERO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SendMoneyServiceTest {
    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK
    lateinit var accountLock: AccountLock

    lateinit var sendMoneyService: SendMoneyService

    @BeforeEach
    internal fun setUp() {
        sendMoneyService =
            SendMoneyService(accountRepository, accountLock, moneyTransferProperties())
    }

    @Test
    fun givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        val sourceAccount = givenSourceAccount(balance = ZERO)
        val targetAccount = givenTargetAccount(balance = ZERO)
        
        val command = SendMoneyCommand(sourceAccount.id, targetAccount.id, Money.of(300L))

        expectLockThenRelease(sourceAccount)

        assertThrows(Exception::class.java) { sendMoneyService.sendMoney(command) }

        verify(exactly = 0) { accountLock.releaseAccount(targetAccount.id) }
    }

    @Test
    fun transactionSucceeds() {
        val sourceAccount = givenSourceAccount(balance = Money.of(1000L))
        val targetAccount = givenTargetAccount(balance = ZERO)

        val command = SendMoneyCommand(sourceAccount.id, targetAccount.id, Money.of(500L))

        expectLockThenRelease(sourceAccount)
        expectLockThenRelease(targetAccount)

        justRun { accountRepository.updateActivities(sourceAccount) }
        justRun { accountRepository.updateActivities(targetAccount) }

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

        every { accountRepository.loadAccount(id, any()) } returns account

        return account
    }

    private fun moneyTransferProperties(): MoneyTransferProperties {
        return MoneyTransferProperties(Money.of(Long.MAX_VALUE))
    }
}