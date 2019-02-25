package service

import model.Account
import model.Accounts
import model.Transfer
import org.jetbrains.exposed.sql.*
import service.DatabaseFactory.dbQuery

class AccountService {

    suspend fun getAllAccounts(): List<Account> = dbQuery {
        Accounts.selectAll().map { toAccount(it) }
    }

    suspend fun getAccount(id: Int): Account? = dbQuery {
        Accounts.select {
            (Accounts.id eq id)
        }.mapNotNull { toAccount(it) }
                .singleOrNull()
    }

    suspend fun makeTransfer(transfer: Transfer) {
        val from = transfer.from
        val to = transfer.to
        val amount = transfer.amount

        val fromAccount = getAccount(from)

        if (fromAccount != null && fromAccount.money >= amount) {
            dbQuery {
                Accounts.update({ Accounts.id eq from }) {
                    with(SqlExpressionBuilder) {
                        it.update(Accounts.money, Accounts.money - amount)
                    }
                }
                Accounts.update({ Accounts.id eq to }) {
                    with(SqlExpressionBuilder) {
                        it.update(Accounts.money, Accounts.money + amount)
                    }
                }
            }
        }
    }


    suspend fun addAccount(defaultMoney: Long): Account {
        var key = 0
        dbQuery {
            key = (Accounts.insert {
                it[money] = defaultMoney
            } get Accounts.id)!!
        }
        return getAccount(key)!!
    }

    suspend fun deleteAccount(id: Int): Boolean {
        return dbQuery {
            Accounts.deleteWhere { Accounts.id eq id } > 0
        }
    }

    private fun toAccount(row: ResultRow): Account =
            Account(
                    id = row[Accounts.id],
                    money = row[Accounts.money]
            )

}
