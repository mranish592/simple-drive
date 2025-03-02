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
        try {
            log.info("$logPrefix received request")
            val loginUserRequest = call.receive<LoginUserRequest>()
            if(log.isDebugEnabled) log.debug("$logPrefix Logging in user with email ${loginUserRequest.email}")
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
            val response = mapOf("accessToken" to accessToken, "name" to existingUser.name)
            log.info("$logPrefix User with email ${loginUserRequest.email} logged in successfully. Sending access token and refresh token.")
            call.response.cookies.append(getRefreshCookie(refreshToken))
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            log.error("$logPrefix Error logging in user", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error logging in user")
        }

    }

    suspend fun signup(call: RoutingCall) {
        val logPrefix = "/auth/signup ::"
        try {
            val signupUserRequest = call.receive<SignupUserRequest>()
            val existingUser = DB.getUserByEmail(signupUserRequest.email)
            if(existingUser != null) {
                log.error("$logPrefix User with email ${signupUserRequest.email} already exists in DB")
                return call.respond(HttpStatusCode.Conflict,
                    "User with email ${signupUserRequest.email} already exists")
            }
            val passwordHash = HashUtil.hashPassword(signupUserRequest.password)
            val user = User(signupUserRequest.name, signupUserRequest.email, passwordHash)
            try {
                DB.createUser(user)
            } catch (e: AlreadyExistsInDBException) {
                log.error("$logPrefix User with email ${signupUserRequest.email} already exists in DB")
                return call.respond(HttpStatusCode.Conflict, "User with email ${signupUserRequest.email} already exists")
            }
            val accessToken = JWTAuthProvider.generateAccessToken(signupUserRequest.email)
            val refreshToken = JWTAuthProvider.generateRefreshToken(signupUserRequest.email)
            val response = mapOf("accessToken" to accessToken, "name" to signupUserRequest.name)
            log.info("$logPrefix User with email ${signupUserRequest.email} signed up successfully. Sending access token and refresh token.")
            call.response.cookies.append(getRefreshCookie(refreshToken))
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            log.error("$logPrefix Error signing up user", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error signing up user")
        }

    }

    suspend fun refresh(call: RoutingCall) {
        val logPrefix = "/auth/refresh ::"
        try {
            val refreshTokenFromCookie = call.request.cookies.get("refreshToken")
            if (refreshTokenFromCookie == null) {
                log.error("$logPrefix No refresh token found in cookie")
                return call.respond(HttpStatusCode.BadRequest, "No refresh token found in cookie")
            }
            val username = JWTAuthProvider.getUsernameFromToken(refreshTokenFromCookie)
            if (username == null) {
                log.error("$logPrefix Invalid refresh token")
                return call.respond(HttpStatusCode.BadRequest, "Invalid refresh token")
            }
            val accessToken = JWTAuthProvider.generateAccessToken(username)
            val refreshToken = JWTAuthProvider.generateRefreshToken(username)
            val response = mapOf("accessToken" to accessToken)
            log.info("$logPrefix User with email ${username} refreshed successfully. Sending access token and refresh token.")
            call.response.cookies.append(getRefreshCookie(refreshToken))
            call.respond(HttpStatusCode.OK, response)
        } catch (e: Exception) {
            log.error("$logPrefix Error refreshing token", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error refreshing token")
        }
    }

    suspend fun logout(call: RoutingCall) {
        val logPrefix = "/auth/logout ::"
        try {
            call.response.cookies.append(getLogoutCookie())
            call.respond(HttpStatusCode.OK, "Logged out successfully")
        } catch (e: Exception) {
            log.error("$logPrefix Error logging out", e)
            return call.respond(HttpStatusCode.InternalServerError, "Error logging out")
        }
    }

    fun getRefreshCookie(refreshToken: String): Cookie {
        return Cookie(name = "refreshToken", value = refreshToken,
            httpOnly = true, secure = false, path = "/auth/refresh",
            expires = Instant.now().plus(Duration.ofDays(30)).toGMTDate())
    }
    fun getLogoutCookie(): Cookie {
        return Cookie(name = "refreshToken", value = "", httpOnly = true, secure = false,
            path = "/auth/refresh", expires = Instant.now().toGMTDate())
    }
}