package com.simpledrive

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.logging.log4j.LogManager
import java.util.*

object ApiRouter {
    val fileStore = FileStore.getFilStore()

    val log = LogManager.getLogger(this.javaClass)
    suspend fun upload(call: RoutingCall) {
        val logPrefix = "/api/upload ::"
        log.info("$logPrefix received request")
        val username = call.principal<String>()!!
        try {
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileId = UUID.randomUUID().toString()
                    val fileName = part.originalFileName ?: UUID.randomUUID().toString()
                    val fileExtension = fileName.substringAfterLast('.', "")
                    val fileProvider = part.provider()
                    val savedFile = fileStore.upload(fileName, fileProvider)
                    if(savedFile == null) {
                        log.error("$logPrefix Error uploading file for $username")
                        return@forEachPart call.respond(HttpStatusCode.InternalServerError, "Error uploading file")
                    }
                    DB.addFile(fileId, fileName, fileExtension, savedFile.size, savedFile.storePath, username)
                    DB.addUserFiles(username, fileId)
                }
                part.dispose()
            }
            log.info("$logPrefix File uploaded successfully for $username")
            return call.respond(HttpStatusCode.OK, "File uploaded successfully")
        } catch (e: Exception) {
            log.error("$logPrefix Error uploading file for $username", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error uploading file")
        }
    }

    suspend fun list(call: RoutingCall) {
        val logPrefix = "/api/list ::"
        try {
            log.info("$logPrefix received request")
            val username = call.principal<String>()!!
            val files = DB.getUserFiles(username)
            if(files == null) {
                log.error("$logPrefix No files found for $username")
                return call.respond(HttpStatusCode.InternalServerError, "Error getting files")
            }
            log.info("$logPrefix Found ${files.size} files for $username")
            call.respond(HttpStatusCode.OK, files)
        } catch (e: Exception) {
            log.error("$logPrefix Error getting files", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error getting files")
        }
    }

    suspend fun download(call: RoutingCall){
        val logPrefix = "/api/download ::"
        log.info("$logPrefix received request")
        val username = call.principal<String>()!!
        val fileId = call.parameters["fileId"]!!
        log.info("$logPrefix Downloading file with fileId $fileId for $username")
        val fileMetaData = DB.getFile(fileId)
        if(fileMetaData == null) {
            log.error("$logPrefix No file found with fileId $fileId")
            return call.respond(HttpStatusCode.BadRequest, "fileId is required")
        }
        if(fileMetaData.username != username) {
            log.error("$logPrefix User $username does not have access to file with fileId $fileId")
            return call.respond(HttpStatusCode.Forbidden, "File not accessible")
        }
        try {
            val fileContent = fileStore.download(fileMetaData.storePath)
            if(fileContent == null) {
                log.error("$logPrefix Error downloading file with fileId $fileId")
                return call.respond(HttpStatusCode.InternalServerError, "Error downloading file")
            }
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, fileMetaData.fileName).toString()
            )
            call.respondFile(fileContent)
        } catch (e: Exception) {
            log.error("$logPrefix Error downloading file with fileId $fileId", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error downloading file")
        }
    }
}