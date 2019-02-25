package model

import org.jetbrains.exposed.sql.Table

object Accounts : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val money = long("money").check { it greaterEq 0L}
}

data class Account(val id: Int, val money: Long)
