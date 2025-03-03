package com.simpledrive.data

import com.simpledrive.utils.Config
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException
import java.util.*

data class SavedFile(val storePath: String, val size: Long)

interface FileStore {
    suspend fun upload(fileName: String, fileProvider: ByteReadChannel): SavedFile?
    suspend fun download(storePath: String): File?
    companion object {
        fun getFilStore(): FileStore {
            return when(Config.FILE_STORE) {
                "LOCAL" -> LocalFileStore
                "S3" -> S3FileStore
                else -> throw Exception("Invalid file store")
            }
        }
    }

}

object LocalFileStore : FileStore {
    private val log = LogManager.getLogger(this.javaClass)
    override suspend fun upload(fileName: String, fileProvider: ByteReadChannel): SavedFile? {

        val logPrefix = "LocalFileStore.upload ::"
        log.info("$logPrefix Uploading $fileName to local file store")
        val dirPath = "/local_data/filestore"
        val uid = UUID.randomUUID().toString()
        val filePath = "$dirPath/$uid-$fileName"
        try {
            val file = File(filePath)
            fileProvider.copyAndClose(file.writeChannel())
            log.info("$logPrefix Uploaded $fileName to $filePath")
            return SavedFile(file.absolutePath, file.length())
        } catch (e: IOException) {
            log.error("$logPrefix Error uploading file $fileName", e)
            return null
        }
    }

    override suspend fun download(storePath: String): File? {
        val logPrefix = "LocalFileStore.download ::"
        log.info("$logPrefix Downloading file from $storePath")
        val file = File(storePath)
        if(!file.exists()) {
            log.error("$logPrefix File $storePath does not exist")
            return null
        }
        log.info("$logPrefix Downloaded file from $storePath")
        return file
    }
}

object S3FileStore : FileStore {
    private val log = LogManager.getLogger(this.javaClass)
    override suspend fun upload(fileName: String, fileProvider: ByteReadChannel): SavedFile {
        val logPrefix = "S3FileStore.upload ::"
        log.info("$logPrefix Uploading $fileName to S3")
        // TODO Upload to S3
        log.info("Uploaded $fileName to S3")
        return SavedFile("s3://$fileName", 0L)
    }

    override suspend fun download(storePath: String): File? {
        val logPrefix = "S3FileStore.download ::"
        log.info("$logPrefix Downloading file from $storePath")
        // TODO Download from S3
        log.info("$logPrefix Downloaded file from $storePath")
        return null
    }
}
