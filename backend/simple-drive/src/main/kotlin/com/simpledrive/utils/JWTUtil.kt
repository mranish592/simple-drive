package com.simpledrive.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Duration
import java.time.Instant

object JWTUtil {
    private val algorithm = Algorithm.HMAC256(Config.JWT_SECRET)
    val verifier = JWT.require(algorithm)
        .withIssuer(Config.JWT_ISSUER)
        .build()

    fun generateAccessToken(username: String): String {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(Instant.now().plus(Duration.ofMinutes(15)))
            .withIssuer(Config.JWT_ISSUER)
            .withAudience(Config.ACCESS_AUD)
            .sign(algorithm)
    }

    fun generateRefreshToken(username: String): String {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(Instant.now().plus(Duration.ofDays(30)))
            .withIssuer(Config.JWT_ISSUER)
            .withAudience(Config.REFRESH_AUD)
            .sign(algorithm)
    }

    fun getUsernameFromToken(token: String): String? {
        return try {
            verifier.verify(token).getClaim("username").asString()
        } catch (e: Exception) {
            null
        }
    }
}