package io.reflectoring.buckpal

import io.reflectoring.buckpal.account.application.port.out.AccountRepository
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Account.AccountId
import io.reflectoring.buckpal.account.domain.Money
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import org.assertj.core.api.BDDAssertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SendMoneySystemTest {
    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Test
    @Sql("SendMoneySystemTest.sql")
    fun sendMoney() {
        val initialSourceBalance = sourceAccount().calculateBalance()
        val initialTargetBalance = targetAccount().calculateBalance()
        val response = whenSendMoney(
            sourceAccountId(),
            targetAccountId(),
            transferredAmount()
        )
        BDDAssertions.then(response.statusCode)
            .isEqualTo(HttpStatus.OK)
        BDDAssertions.then(sourceAccount().calculateBalance())
            .isEqualTo(initialSourceBalance.minus(transferredAmount()))
        BDDAssertions.then(targetAccount().calculateBalance())
            .isEqualTo(initialTargetBalance.plus(transferredAmount()))
    }

    private fun sourceAccount(): Account {
        return loadAccount(sourceAccountId())
    }

    private fun targetAccount(): Account {
        return loadAccount(targetAccountId())
    }

    private fun loadAccount(accountId: AccountId): Account {
        return accountRepository.loadAccount(
            accountId,
            LocalDateTime.now()
        )
    }

    private fun whenSendMoney(
        sourceAccountId: AccountId,
        targetAccountId: AccountId,
        amount: Money
    ): ResponseEntity<*> {
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json")
        val request = HttpEntity<Void>(null, headers)
        return restTemplate.exchange(
            "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
            HttpMethod.POST,
            request,
            Any::class.java,
            sourceAccountId.value,
            targetAccountId.value,
            amount.amount
        )
    }

    private fun transferredAmount(): Money {
        return of(500L)
    }

    private fun balanceOf(accountId: AccountId): Money {
        val account = accountRepository.loadAccount(accountId, LocalDateTime.now())
        return account.calculateBalance()
    }

    private fun sourceAccountId(): AccountId {
        return AccountId(1L)
    }

    private fun targetAccountId(): AccountId {
        return AccountId(2L)
    }
}