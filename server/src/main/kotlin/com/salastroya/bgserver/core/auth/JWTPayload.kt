package com.salastroya.bgserver.core.auth

import java.time.Instant

const val ISSUER = "bgserver"

data class JWTPayload(
    val userId: Long,
    val isAdmin: Boolean = false,
    val iss: String = ISSUER,
    val iat: Instant = Instant.now()
)