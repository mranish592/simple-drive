package com.simpledrive

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import org.slf4j.event.Level
import java.time.Duration
import java.time.Instant
import io.ktor.server.plugins.cors.routing.*

fun main(): Unit = runBlocking {
    val log = LogManager.getLogger()
    log.info("Starting simple drive backend.")
    val server = embeddedServer(Netty, port = 3000, host = "0.0.0.0") {
        module()
    }
    server.start(wait = true)
}

fun Application.module() {
    Server.configure(this)
    Router.routing(this)
}
object JWTAuthProvider {
    val secret = "your-secret"
    val jwtIssuer = "simpldrive-backend"
    val accessAudience = "simledrive-acesss"
    val refreshAudience = "simpledrive-refresh"
    val algorithm = Algorithm.HMAC256(secret)
    val verifier = JWT.require(algorithm)
        .withIssuer(jwtIssuer)
        .build()

    fun generateAccessToken(username: String): String {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(Instant.now().plus(Duration.ofMinutes(15)))
            .withIssuer(jwtIssuer)
            .withAudience(accessAudience)
            .sign(algorithm)
    }

    fun generateRefreshToken(username: String): String {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(Instant.now().plus(Duration.ofDays(30)))
            .withIssuer(jwtIssuer)
            .withAudience(refreshAudience)
            .sign(algorithm)
    }

//    fun verifyToken(token: String): Boolean {
//        return try {
//            verifier.verify(token)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            verifier.verify(token).getClaim("username").asString()
        } catch (e: Exception) {
            null
        }
    }
}

//data class UserPrincipal(val userId: String)
object Server {
    fun configure(application: Application) {
        application.install(CallLogging) { level = Level.INFO }
        application.install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowMethod(io.ktor.http.HttpMethod.Post)
            allowMethod(io.ktor.http.HttpMethod.Get)
            allowHeader(io.ktor.http.HttpHeaders.Authorization)
            allowHeader(io.ktor.http.HttpHeaders.ContentType)
            allowHeader(io.ktor.http.HttpHeaders.Cookie)
            allowHeader(HttpHeaders.AccessControlAllowOrigin)
            allowHeader(HttpHeaders.AccessControlAllowHeaders)
            allowHeader("refreshToken")
            allowHeader("accessToken")
            allowXHttpMethodOverride()
            allowOrigins { true }
            allowCredentials = true
            anyHost()
            allowHost("localhost:5173", schemes = listOf("http"))
        }
        application.install(Authentication) { authenticationConfig(this) }
        application.install(ContentNegotiation) { jackson() }
    }

    private fun authenticationConfig(authenticationConfig: AuthenticationConfig,) {
        authenticationConfig.jwt("bearer") {
            verifier(JWTAuthProvider.verifier)
            validate { credential -> credential.payload.getClaim("username").asString() }
        }
    }
}



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


data class SignupUserRequest(val email: String, val password: String, val name: String)
data class LoginUserRequest(val email: String, val password: String)



