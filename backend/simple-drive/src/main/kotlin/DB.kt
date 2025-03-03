package com.simpledrive

import com.mongodb.MongoClientException
import com.mongodb.MongoException
import io.ktor.server.plugins.*
import org.apache.logging.log4j.LogManager
import org.litote.kmongo.reactivestreams.*
import org.litote.kmongo.coroutine.*

data class User(val name: String, val username: String, val passwordHash: String, val fileIds: List<String> = emptyList(), )
data class File(val fileId: String, val fileName: String, val extension: String, val size: Long, val storePath: String, val username: String, val createdOn: Long = System.currentTimeMillis())
class AlreadyExistsInDBException(message: String) : Exception(message)
object DB {
    var client: CoroutineClient
    private val log = LogManager.getLogger(this.javaClass)
    init {
        val username = "admin"
        val password = "admin"
        val host = "localhost"
        val port = 27017
        val connectionString = "mongodb://$username:$password@$host:$port"
        println("Connecting to MongoDB using $connectionString")
        client = KMongo.createClient(connectionString).coroutine
        println("Connected to MongoDB using $connectionString")

    }
    private val db = client.getDatabase("simple-drive")
    private val users = db.getCollection<User>("users")
    private val files = db.getCollection<File>("files")


    suspend fun createUser(user: User) {
        val logPrefix = "DB.createUser ::"
        try {
            val existingUser = users.findOne("{'username': '${user.username}'}")
            if(existingUser != null) {
                log.error("$logPrefix User with username ${user.username} already exists")
                throw AlreadyExistsInDBException("User already exists in DB. Cannot create user")
            }
            users.insertOne(user)
            if(log.isDebugEnabled) log.debug("$logPrefix User created successfully with username ${user.username} and name ${user.name}")
        } catch (e: MongoException) {
            log.error("$logPrefix Error creating user", e)
            throw e
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        val logPrefix = "DB.getUserByEmail ::"
        return try {
            log.info("$logPrefix Getting user by email $email")
            val user = users.findOne("{'username': '$email'}")
            log.info("$logPrefix got user by email $email")
            user
        } catch (e: Throwable) {
            log.error("$logPrefix Error getting user by email", e)
            null
        }
    }

    suspend fun getUserFiles(username: String): List<File>? {
        val logPrefix = "DB.getUserFiles ::"
        return try {
            files.find("{'username': '$username'}").toList()
        } catch (e: Throwable) {
            log.error("$logPrefix Error getting user files", e)
            null
        }
    }

    suspend fun addFile(fileId: String, fileName: String, extension: String, size: Long, storePath: String, username: String) {
        val logPrefix = "DB.addFile ::"
        try {
            val existingFile = files.findOne("{'fileId': '${fileId}'}")
            if(existingFile != null) {
                log.error("$logPrefix File with fileId $fileId already exists")
                throw AlreadyExistsInDBException("File already exists in DB. Cannot create File")
            }
            files.insertOne(File(fileId, fileName, extension, size, storePath, username))
            if(log.isDebugEnabled) log.debug("$logPrefix File created successfully with fileId $fileId and name $fileName")
        } catch (e: MongoException) {
            log.error("$logPrefix Error creating file with name: $fileName and fileId: $fileId", e)
            throw e
        }
    }

    suspend fun addUserFiles(username: String?, fileId: String) {
        val logPrefix = "DB.addUserFiles ::"
        try {
            val user = users.findOne("{'username': '$username'}")
            if(user == null) {
                log.error("$logPrefix User not found with username $username")
                throw NotFoundException("User not found")
            }
//            val previousFileids = user.fileIds.joinToString(",") { "'$it'" }
            val fileIds = user.fileIds.plus(fileId).joinToString (",") { "'$it'" }
//            log.info("$logPrefix previousFileids: $previousFileids")
            users.updateOne("{'username': '$username'}", "{\$set: {fileIds: [$fileIds]}}")
        } catch (e: Throwable) {
            log.error("$logPrefix Error adding files for user $username", e)
            throw e
        }
    }

    suspend fun getFile(fileId: String): File? {
        val logPrefix = "DB.getFile ::"
        return try {
            files.findOne("{'fileId': '$fileId'}")
        } catch (e: Throwable) {
            log.error("$logPrefix Error getting file with fileId $fileId", e)
            null
        }
    }

}