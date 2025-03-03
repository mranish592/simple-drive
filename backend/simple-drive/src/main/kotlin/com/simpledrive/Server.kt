package com.simpledrive

import com.simpledrive.routes.Router
import com.simpledrive.utils.JWTUtil
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import org.slf4j.event.Level

fun Application.module() {
    Server.configure(this)
    Router.routing(this)
}


//data class UserPrincipal(val userId: String)
object Server {
    fun configure(application: Application) {
        application.install(CallLogging) { level = Level.INFO }
        application.install(CORS) {
//            allowMethod(HttpMethod.Options)
//            allowMethod(HttpMethod.Put)
//            allowMethod(HttpMethod.Delete)
//            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Get)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Cookie)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.AccessControlAllowHeaders)
            allowHeader("refreshToken")
            allowOrigins { true }
            allowCredentials = true
            anyHost()
            allowHost("localhost:5173", schemes = listOf("http"))
        }
        application.install(Authentication) { authenticationConfig(this) }
        application.install(ContentNegotiation) { jackson() }
    }

    private fun authenticationConfig(authenticationConfig: AuthenticationConfig) {
        authenticationConfig.jwt("bearer") {
            verifier(JWTUtil.verifier)
            validate { credential -> credential.payload.getClaim("username").asString() }
        }
    }
}