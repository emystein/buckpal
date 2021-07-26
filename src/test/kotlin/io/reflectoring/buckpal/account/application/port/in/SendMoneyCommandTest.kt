package io.reflectoring.buckpal.account.application.port.`in`

import io.mockk.every
import io.mockk.mockk
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Money
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SendMoneyCommandTest {
    private val sourceAccount: Account = givenSourceAccount()
    private val targetAccount: Account = givenTargetAccount()

    @Test
    fun rejectZeroMoneyTransaction() {
        Assertions.assertThrows(NotPositiveAmountException::class.java) {
            SendMoneyCommand(
                sourceAccount.id,
                targetAccount.id,
                Money.ZERO
            )
        }
    }

    @Test
    fun rejectNegativeMoneyTransaction() {
        Assertions.assertThrows(NotPositiveAmountException::class.java) {
            SendMoneyCommand(
                sourceAccount.id,
                targetAccount.id,
                Money.of(-100)
            )
        }
    }

    private fun givenSourceAccount(): Account {
        return givenAnAccountWithId(Account.AccountId(41L))
    }

    private fun givenTargetAccount(): Account {
        return givenAnAccountWithId(Account.AccountId(42L))
    }

    private fun givenAnAccountWithId(id: Account.AccountId): Account {
        val account = mockk<Account>()

        every { account.id } returns id

        return account
    }
}