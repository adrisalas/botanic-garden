package com.salastroya.bgserver.core.auth.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY

data class User(
    val username: String,
    @JsonProperty(access = WRITE_ONLY)
    val password: String,
    @JsonProperty(access = WRITE_ONLY)
    val salt: String,
    val isAdmin: Boolean = false
)