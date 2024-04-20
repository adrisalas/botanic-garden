package com.salastroya.bgserver.core.auth

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*


@ExtendWith(MockKExtension::class)
class JWTUseCasesTest {
    companion object {
        private const val VALID_TOKEN =
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9" +
                    ".eyJ1c2VySWQiOjEwMCwiaXNBZG1pbiI6dHJ1ZSwiaXNzIjoiYmdzZXJ2ZXIiLCJpYXQiOjEuNzEzNTcxMTY0MzU4MjU3RTl9" +
                    ".MjhaS4WSnso3Clf68k7PqXBx_xUexQ-jIH3MgmwRFz75ae8anYhujyjiA3fJwPHTgrh4Zx5n5eRJQlifOtv69w"

        private const val INVALID_TOKEN =
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9" +
                    ".eyJ1c2VySWQiOjIyMiwiaXNBZG1pbiI6dHJ1ZSwiaXNzIjoiYmdzZXJ2ZXIiLCJpYXQiOjE3MTM1NzExNjQuMzU4MjU3fQ" +
                    ".MjhaS4WSnso3Clf68k7PqXBx_xUexQ-jIH3MgmwRFz75ae8anYhujyjiA3fJwPHTgrh4Zx5n5eRJQlifOtv69w"
    }

    private val key = "Patata2024"
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

    private val service: JWTUseCases = JWTUseCases(key, objectMapper)

    @Test
    fun createToken() {
        val jwt = service.createToken(100L, false)

        val (header, payload, _) = jwt.split(".")

        assertThat(String(Base64.getDecoder().decode(header)))
            .isEqualTo("{\"alg\":\"HS512\",\"typ\":\"JWT\"}")
        assertThat(String(Base64.getDecoder().decode(payload)))
            .contains("{\"userId\":100,\"isAdmin\":false,\"iss\":\"bgserver\",\"iat\":")
    }

    @Test
    fun decodeValidToken() {
        val jwt = service.decodeToken(VALID_TOKEN)

        assertThat(jwt.iat).isEqualTo(Instant.ofEpochSecond(1713571164L, 358257000L))
        assertThat(jwt.iss).isEqualTo(ISSUER)
        assertThat(jwt.userId).isEqualTo(100)
        assertThat(jwt.isAdmin).isEqualTo(true)
    }

    @Test
    fun decodeInValidToken() {
        assertThrows<JWTVerificationException> { service.decodeToken(INVALID_TOKEN) }
    }
}