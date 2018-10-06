package net.mixaal.poc.kotlin

/**
 * Usage:
 *   mvn clean package
 *   java -jar target/kotlin-rest-service-1.0-SNAPSHOT-jar-with-dependencies.jar
 *
 *   curl -kv -X POST 'http://localhost:4567/customers/?name=mixaal&email=mixaal@google'
 *   curl -kv -X POST 'http://localhost:4567/customers/?name=pixaal&email=pixaal@google'
 *   curl -kv -X GET http://localhost:4567/customers/
 *   curl -kv -X GET http://localhost:4567/customers/1
 *   curl -kv -X DELETE http://localhost:4567/customers/1
 *
 */

import com.google.gson.Gson
import spark.Spark.*
import java.util.concurrent.atomic.AtomicInteger

val customers = hashMapOf<Int, Customer>()
val lastId  : AtomicInteger = AtomicInteger(customers.size - 1)

data class Customer(val id: Int, val name: String, val email: String)

fun save(name: String, email: String) {
    val id = lastId.incrementAndGet()
    customers.put(id, Customer(name = name, email = email, id = id))
}

fun findById(id: Int): Customer? {
    return customers[id]
}

fun findByEmail(email: String): Customer? {
    return customers.values.find { it.email == email }
}

fun remove(id: Int) {
    customers.remove(id)
}

val gson = Gson()

fun Any.toJSON(): String {
    return gson.toJson(this)
}

fun main(args: Array<String>) {
    path("/customers") {
        get("/") {
            request, response -> customers.toJSON()
        }
        get("/:id") {request, response -> findById(request.params("id").toInt())!!.toJSON() }

        post("/create") { req, res ->
            save(name = req.queryParams("name"), email = req.queryParams("email"))
            res.status(201)
            "ok"
        }

        delete("/:id") {
            req, res -> remove(req.params("id").toInt())
            res.status(204)
        }
    }

}

