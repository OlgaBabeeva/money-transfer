package service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Accounts
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())
        transaction {
            create(Accounts)
            Accounts.insert {
                it[money] = 100
            }
            Accounts.insert {
                it[money] = 200
            }
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        config.username = "sa"
        config.password = "sa"
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
            block: () -> T): T =
            withContext(Dispatchers.IO) {
                transaction { block() }
            }


}