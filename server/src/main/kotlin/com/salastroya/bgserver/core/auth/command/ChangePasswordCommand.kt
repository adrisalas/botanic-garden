package com.salastroya.bgserver.core.auth.command

data class ChangePasswordCommand(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)