package com.salastroya.bgserver.infrastructure.auth.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("botanic_user")
class UserDto(
    @Id val username: String,
    val password: String,
    val salt: String,
    val isAdmin: Boolean
)