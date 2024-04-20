package com.salastroya.bgserver.core.auth.model

import java.time.Instant

const val ISSUER = "bgserver"

data class JWTPayload(
    val username: String,
    val isAdmin: Boolean = false,
    val iss: String = ISSUER,
    val iat: Instant = Instant.now()
)