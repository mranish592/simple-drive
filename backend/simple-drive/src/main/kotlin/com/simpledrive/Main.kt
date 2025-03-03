package com.simpledrive

import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(): Unit = runBlocking {
    val log = LogManager.getLogger()
    log.info("Starting simple drive backend on port 3000")
    val server = embeddedServer(Netty, port = 3000, host = "0.0.0.0") {
        module()
    }
    server.start(wait = true)
}











