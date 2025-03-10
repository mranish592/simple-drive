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
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.apache.logging.log4j.LogManager
import org.slf4j.event.Level
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

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

data class RequestCounter(var counts: AtomicInteger, var issuedAt: Instant)

val RateLimitter = createRouteScopedPlugin(name = "RateLimitter") {
    val log = LogManager.getLogger(this.javaClass)
    val uploadCounts = HashMap<String, RequestCounter>()
    val downloadCounts = HashMap<String, RequestCounter>()
//    for invoking middleware after authentication middleware is done
    on(AuthenticationChecked) { call ->
//        get the username from authentication middleware
        val username = call.authentication.principal<String>()!!
        val path = call.request.path()
        when(path){
            "/api/upload" -> {
                var requestCounter = uploadCounts[username]
                if(requestCounter == null) {
                    requestCounter = RequestCounter(AtomicInteger(5), Instant.now())
                    uploadCounts[username] = requestCounter
                }
                if(requestCounter.counts.get() < 1) {
                    if(requestCounter.issuedAt.isAfter(Instant.now().minusSeconds(60))){
                        if(log.isDebugEnabled) log.info("limit exceeded for user $username")
                        return@on call.respond(HttpStatusCode.TooManyRequests, "Upload quota exceeded")
                    }
                    uploadCounts[username] = RequestCounter(AtomicInteger(5), Instant.now())
                }
                requestCounter.counts.decrementAndGet()
            }
            "/api/download" -> {
                var requestCounter = downloadCounts[username]
                if(requestCounter == null) {
                    requestCounter = RequestCounter(AtomicInteger(5), Instant.now())
                    downloadCounts[username] = requestCounter
                }
                if(requestCounter.counts.get() < 1) {
                    if(requestCounter.issuedAt.isAfter(Instant.now().minusSeconds(60))){
                        if(log.isDebugEnabled) log.info("limit exceeded for user $username")
                        return@on call.respond(HttpStatusCode.TooManyRequests, "Download quota exceeded")
                    }
                    downloadCounts[username] = RequestCounter(AtomicInteger(5), Instant.now())
                }
                requestCounter.counts.decrementAndGet()
            }
        }
    }
}