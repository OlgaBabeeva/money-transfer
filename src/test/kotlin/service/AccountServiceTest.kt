package service

import kotlinx.coroutines.runBlocking
import model.Account
import model.Transfer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.ArrayList


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTest {

    private val accountService = AccountService()

    @BeforeAll
    fun init() {
        DatabaseFactory.init()
    }

    @Test
    fun testAddAccount() = runBlocking {
        // when
        val saved = accountService.addAccount(100)
        // then
        val retrieved = accountService.getAccount(saved.id)
        assertThat(retrieved).isEqualTo(saved)
        assertThat(retrieved?.id).isEqualTo(saved.id)
        assertThat(retrieved?.money).isEqualTo(saved.money)

        Unit
    }

    @Test
    fun testTransfer() = runBlocking {
        val from = accountService.addAccount(100)
        val to = accountService.addAccount(100)

        val transfer = Transfer(from.id, to.id, 100)

        accountService.makeTransfer(transfer)

        val fromAfterTransfer = accountService.getAccount(from.id)
        val toAfterTransfer = accountService.getAccount(to.id)
        assertThat(fromAfterTransfer?.money).isEqualTo(0)
        assertThat(toAfterTransfer?.money).isEqualTo(200)

        Unit
    }

    @Test
    fun testTransferNotEnoughMoney() = runBlocking {
        val from = accountService.addAccount(100)
        val to = accountService.addAccount(100)

        val transfer = Transfer(from.id, to.id, 200)

        accountService.makeTransfer(transfer)

        val fromAfterTransfer = accountService.getAccount(from.id)
        val toAfterTransfer = accountService.getAccount(to.id)
        assertThat(fromAfterTransfer?.money).isEqualTo(100)
        assertThat(toAfterTransfer?.money).isEqualTo(100)

        Unit
    }


}