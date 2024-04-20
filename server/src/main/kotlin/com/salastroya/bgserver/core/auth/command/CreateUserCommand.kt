package com.salastroya.bgserver.core.auth.command

data class CreateUserCommand(
    val username: String,
    val password: String
)