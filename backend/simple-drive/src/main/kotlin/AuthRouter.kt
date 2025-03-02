package com.simpledrive

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.apache.logging.log4j.LogManager
import java.time.Duration
import java.time.Instant

object AuthRouter {
    private val log = LogManager.getLogger(this.javaClass)

    suspend fun login(call: RoutingCall) {
        val logPrefix = "/auth/login ::"
        val loginUserRequest = call.receive<LoginUserRequest>()
        val existingUser = DB.getUserByEmail(loginUserRequest.email)
        if(existingUser == null) {
            log.error("$logPrefix No user found with email ${loginUserRequest.email} in DB")
            return call.respond(HttpStatusCode.NotFound, "User not found")
        }
        if(!HashUtil.verifyPassword(loginUserRequest.password, existingUser.passwordHash)) {
            log.error("$logPrefix Invalid password for user with email ${loginUserRequest.email}")
            return call.respond(HttpStatusCode.Unauthorized, "Invalid email or password")
        }
        val accessToken = JWTAuthProvider.generateAccessToken(loginUserRequest.email)
        val refreshToken = JWTAuthProvider.generateRefreshToken(loginUserRequest.email)
        val response = mapOf("accessToken" to accessToken)
        val cookie = Cookie(
            name = "refreshToken",
            value = refreshToken,
            httpOnly = true,
            secure = false,
            path = "/auth/refresh",
            expires = Instant.now().plus(Duration.ofDays(30)).toGMTDate() // Adjust expiration
        )
        log.info("$logPrefix User with email ${loginUserRequest.email} logged in successfully. Sending access token and refresh token.")
        call.response.cookies.append(cookie)
        call.respond(HttpStatusCode.OK, response)
    }

    suspend fun signup(call: RoutingCall) {
        val logPrefix = "/auth/signup ::"
        val signupUserRequest = call.receive<SignupUserRequest>()
        val existingUser = DB.getUserByEmail(signupUserRequest.email)
        if(existingUser != null) {
            log.error("$logPrefix User with email ${signupUserRequest.email} already exists in DB")
            return call.respond(HttpStatusCode.Conflict,
                "User with email ${signupUserRequest.email} already exists")
        }
        val passwordHash = HashUtil.hashPassword(signupUserRequest.password)
        val user = User(signupUserRequest.name, signupUserRequest.email, passwordHash)
        DB.createUser(user)
        val accessToken = JWTAuthProvider.generateAccessToken(signupUserRequest.email)
        val refreshToken = JWTAuthProvider.generateRefreshToken(signupUserRequest.email)
        val response = mapOf("accessToken" to accessToken)
        val cookie = Cookie(
            name = "refreshToken",
            value = refreshToken,
            httpOnly = true,
            secure = false,
            path = "/auth/refresh",
            expires = Instant.now().plus(Duration.ofDays(30)).toGMTDate()
        )
        log.info("$logPrefix User with email ${signupUserRequest.email} signed up successfully. Sending access token and refresh token.")
        call.response.cookies.append(cookie)
        call.respond(HttpStatusCode.OK, response)
    }

    suspend fun refresh(call: RoutingCall) {
        val logPrefix = "/auth/refresh ::"
        val refreshTokenFromCookie = call.request.cookies.get("refreshToken")
        if(refreshTokenFromCookie == null) {
            log.error("$logPrefix No refresh token found in cookie")
            return call.respond(HttpStatusCode.BadRequest, "No refresh token found in cookie")
        }
        val username = JWTAuthProvider.getUsernameFromToken(refreshTokenFromCookie)
        if(username == null) {
            log.error("$logPrefix Invalid refresh token")
            return call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")
        }
        val accessToken = JWTAuthProvider.generateAccessToken(username)
        val refreshToken = JWTAuthProvider.generateRefreshToken(username)
        val response = mapOf("accessToken" to accessToken)
        val cookie = Cookie(
            name = "refreshToken",
            value = refreshToken,
            httpOnly = true,
            secure = false,
            path = "/auth/refresh",
            expires = Instant.now().plus(Duration.ofDays(30)).toGMTDate()
        )
        log.info("$logPrefix User with email ${username} refreshed successfully. Sending access token and refresh token.")
        call.response.cookies.append(cookie)
        call.respond(HttpStatusCode.OK, response)
    }
}