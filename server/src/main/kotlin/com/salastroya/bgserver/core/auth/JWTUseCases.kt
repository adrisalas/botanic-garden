package com.salastroya.bgserver.core.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JWTUseCases(
    @Value("\${custom.jwtKey}") private val key: String,
    private val objectMapper: ObjectMapper
) {
    private val algorithm: Algorithm by lazy { Algorithm.HMAC512(key) }

    fun createToken(userId: Long, isAdmin: Boolean): String {
        val payload = JWTPayload(userId, isAdmin)
        return JWT.create()
            .withPayload(objectMapper.writeValueAsString(payload))
            .sign(algorithm)
    }

    @Throws(JWTVerificationException::class)
    fun decodeToken(jwt: String): JWTPayload {
        val payload = JWT.require(algorithm)
            .withIssuer(ISSUER)
            .build()
            .verify(jwt)
            .payload

        val json = String(Base64.getDecoder().decode(payload))

        return objectMapper.readValue(json, JWTPayload::class.java)
    }


}