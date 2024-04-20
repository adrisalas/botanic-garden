package com.salastroya.bgserver.infrastructure.auth.mapper

import com.salastroya.bgserver.core.auth.model.User
import com.salastroya.bgserver.infrastructure.auth.dto.UserDto

fun UserDto.toModel(): User {
    return User(
        username = this.username,
        password = this.password,
        salt = this.salt,
        isAdmin = this.isAdmin
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        username = this.username,
        password = this.password,
        salt = this.salt,
        isAdmin = this.isAdmin
    )
}