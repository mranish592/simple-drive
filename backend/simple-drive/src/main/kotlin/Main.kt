package com.simpledrive

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess


fun main(): Unit = runBlocking {
    val log = LogManager.getLogger()
    log.info("Starting simple drive backend.")
    val vertx = Vertx.vertx()
    vertx.deployVerticle(ServerVerticle()).coAwait()
}