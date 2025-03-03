package com.simpledrive.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

object Router {
    fun routing(application: Application) {
        application.routing {
            route("/auth") {
                post("/signup") { AuthRouter.signup(call) }
                post("/login") { AuthRouter.login(call)}
                post("/refresh") { AuthRouter.refresh(call) }
                post("/logout") { AuthRouter.logout(call) }
            }
        }
        application.routing {
            authenticate("bearer") {
                route("/api") {
                    post("/upload") { ApiRouter.upload(call) }
                    get("/download") { ApiRouter.download(call) }
                    get("/list") { ApiRouter.list(call) }
                }
            }
        }
    }
}