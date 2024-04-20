package com.salastroya.bgserver.core.auth.model

const val ISSUER = "bgserver"

data class JWTPayload(
    val username: String,
    val isAdmin: Boolean = false,
    val iss: String = ISSUER,
    val iat: Long = System.currentTimeMillis() / 1_000
)