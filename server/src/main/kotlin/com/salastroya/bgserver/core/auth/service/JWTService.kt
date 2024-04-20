package com.salastroya.bgserver.core.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.salastroya.bgserver.core.auth.model.ISSUER
import com.salastroya.bgserver.core.auth.model.JWTPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JWTService(
    @Value("\${custom.security.passphrase}") private val secret: String,
    private val objectMapper: ObjectMapper
) {
    private val algorithm: Algorithm by lazy { Algorithm.HMAC512(secret) }

    fun createToken(username: String, isAdmin: Boolean): String {
        val payload = JWTPayload(username, isAdmin)
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