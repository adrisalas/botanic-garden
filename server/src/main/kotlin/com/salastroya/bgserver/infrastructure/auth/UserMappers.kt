package com.salastroya.bgserver.infrastructure.auth

import com.salastroya.bgserver.core.auth.model.User

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