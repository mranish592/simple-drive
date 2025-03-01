package com.simpledrive

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess


fun main()  = runBlocking {
    val log = LogManager.getLogger()
    log.info("Starting simple drive backend.")
    val port = 3000

    val vertx = Vertx.vertx()
    val router = Router.router(vertx)
    router.route("/").handler { ctx ->
        log.info("request received on / sending 200")
        ctx.response().setStatusCode(200).end(JsonObject().toBuffer())
    }

    try {
        val server = vertx.createHttpServer()
            .requestHandler(router)
            .listen(port).coAwait()
        log.info("Server started on port ${server.actualPort()}")
    } catch (e: Exception) {
        log.error("Failed to start server", e)
        exitProcess(-1)
    }

}