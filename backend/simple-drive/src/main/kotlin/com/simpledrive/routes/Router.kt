package com.simpledrive.routes

import com.simpledrive.RateLimitter
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
//                    install the Ratelimiter here since it should be invoked after authenticate blog.
//                    Example: https://github.com/ktorio/ktor-documentation/blob/3.1.0/codeSnippets/snippets/custom-plugin-authorization/src/main/kotlin/com/example/Application.kt
                    install(RateLimitter)
                    post("/upload") { ApiRouter.upload(call) }
                    get("/download") { ApiRouter.download(call) }
                    get("/list") { ApiRouter.list(call) }
                }
            }
        }
    }
}