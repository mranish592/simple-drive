package com.simpledrive

import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

data class User(val name: String, val email: String, val passwordHash: String)

object DB {
    var db : CoroutineDatabase
    init {
        val username = "admin"
        val password = "admin"
        val host = "localhost"
        val port = 27017
        val connectionString = "mongodb://$username:$password@$host:$port"
        println("Connecting to MongoDB using $connectionString")
        val client = KMongo.createClient(connectionString).coroutine
        println("Connected to MongoDB using $connectionString")

        db = client.getDatabase("simple-drive")
    }
    val users = db.getCollection<User>()

    suspend fun createUser(user: User) {
        users.insertOne(user)
    }

    suspend fun getUserByEmail(email: String): User? {
        return users.findOne("{'email': '$email'}")
    }

}