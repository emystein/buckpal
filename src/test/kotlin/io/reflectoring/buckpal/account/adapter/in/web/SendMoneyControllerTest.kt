package io.reflectoring.buckpal.account.adapter.`in`.web

import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyCommand
import io.reflectoring.buckpal.account.application.port.`in`.SendMoneyUseCase
import io.reflectoring.buckpal.account.domain.Account
import io.reflectoring.buckpal.account.domain.Money
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [SendMoneyController::class])
class SendMoneyControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var sendMoneyUseCase: SendMoneyUseCase

    @Test
    @Throws(Exception::class)
    fun testSendMoney() {
        val command = SendMoneyCommand(Account.AccountId(41L), Account.AccountId(42L), Money.of(500L))

        given(sendMoneyUseCase.sendMoney(command)).willReturn(true)

        val url = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}"

        mockMvc.perform(post(url, 41L, 42L, 500)
                .header("Content-Type", "application/json"))
            .andExpect(status().isOk())

        then(sendMoneyUseCase).should().sendMoney(command)
    }
}