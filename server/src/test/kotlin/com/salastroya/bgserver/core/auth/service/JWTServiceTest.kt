package com.salastroya.bgserver.core.auth.service

import com.auth0.jwt.exceptions.JWTVerificationException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.salastroya.bgserver.core.auth.model.ISSUER
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class JWTServiceTest {
    companion object {
        private const val USERNAME = "adri"
        private const val PASSPHRASE = "Potato2024"
        private const val VALID_TOKEN =
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9" +
                    ".eyJ1c2VybmFtZSI6ImFkcmkiLCJpc0FkbWluIjp0cnVlLCJpc3MiOiJiZ3NlcnZlciIsImlhdCI6MTcxMzY1MjQ2M30" +
                    ".A1f01-zTRm9t2ddbC_EZg1_5_7abhtyEjXsWNbbP_CgjT_POhTg462S7UXOSo8KbOtICVRrcSKTKsEl0cNuFRw"

        private const val INVALID_TOKEN =
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9" +
                    ".eyJ1c2VybmFtZSI6ImFkcmkiLCJpc0FkbWluIjpmYWxzZSwiaXNzIjoiYmdzZXJ2ZXIiLCJpYXQiOjEuNzEzNjExNDY4MDE3OTM5RTl9" +
                    ".FB2-9blqHv1vsj2cE4PVvOVibhWVfFjJbOFE4xp8D4Sc6J_OWsCj_YznnhQyySr6BiO_7j-b-1UB8aeIKInp7Q"
    }

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    private val service: JWTService = JWTService(PASSPHRASE, objectMapper)

    @Test
    fun createToken() {
        val jwt = service.createToken(USERNAME, false)

        val (header, payload, _) = jwt.split(".")

        assertThat(String(Base64.getDecoder().decode(header)))
            .isEqualTo("{\"alg\":\"HS512\",\"typ\":\"JWT\"}")
        assertThat(String(Base64.getDecoder().decode(payload)))
            .contains("{\"username\":\"adri\",\"isAdmin\":false,\"iss\":\"bgserver\",\"iat\":")
    }

    @Test
    fun decodeValidToken() {
        val jwt = service.decodeToken(VALID_TOKEN)

        assertThat(jwt.iat).isEqualTo(1713652463L)
        assertThat(jwt.iss).isEqualTo(ISSUER)
        assertThat(jwt.username).isEqualTo(USERNAME)
        assertThat(jwt.isAdmin).isEqualTo(true)
    }

    @Test
    fun decodeInValidToken() {
        assertThrows<JWTVerificationException> { service.decodeToken(INVALID_TOKEN) }
    }
}