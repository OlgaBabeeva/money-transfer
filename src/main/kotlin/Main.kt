import com.google.gson.Gson
import spark.Request
import spark.Spark.*

import kotlinx.coroutines.runBlocking
import model.Transfer
import service.AccountService
import service.DatabaseFactory

fun Request.qp(key: String): String = this.queryParams(key)

fun main() {

    DatabaseFactory.init()

    var gson = Gson()

    val accountService = AccountService()

    path("/accounts") {

        get("") { req, res ->
            runBlocking {
                gson.toJson(accountService.getAllAccounts())
            }
        }

        get("/:id") { req, res ->
            runBlocking {
                gson.toJson(accountService.getAccount(req.params("id").toInt()))
            }
        }

        post("/create") { req, res ->
            runBlocking {
                accountService.addAccount(req.qp("money").toLong())
            }
            res.status(201)
            "ok"
        }

        post("/delete") { req, res ->
            runBlocking {
                accountService.deleteAccount(req.qp("id").toInt())
            }
            res.status(201)
            "ok"
        }

    }

    post("/transfer") { req, res ->
        val from = req.qp("from").toInt()
        val to = req.qp("to").toInt()
        val amount = req.qp("amount").toLong()

        runBlocking {
            accountService.makeTransfer(Transfer(from, to, amount))
        }

        res.status(201)
        "ok"
    }


}

