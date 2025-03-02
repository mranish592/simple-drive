package com.simpledrive

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess

class ServerVerticle(val port: Int = 3000): CoroutineVerticle() {
    val log = LogManager.getLogger(this.javaClass)
    override suspend fun start() {
        log.info("Starting server verticle")
        try {
            val router = Router.router(vertx)
            router.route("/").handler { ctx ->
                log.info("request received on / sending 200 ")
                ctx.response().setStatusCode(200).end(JsonObject().toBuffer())
            }

            val server = vertx.createHttpServer()
                .requestHandler(router)
                .listen(port).coAwait()
            log.info("Server started on port ${server.actualPort()}")

            Runtime.getRuntime().addShutdownHook(Thread {
                log.info("Shutting down server...")
                server.close()
                    .onSuccess {
                        log.info("Server shutdown")
                    }
                    .onFailure {
                        log.error("Failed to shutdown server", it)
                    }
            })
        } catch (e: Exception) {
            log.error("Failed to start server", e)
            exitProcess(-1)
        }
    }
}