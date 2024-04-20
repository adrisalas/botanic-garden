package com.salastroya.bgserver.application

import com.salastroya.bgserver.core.auth.service.JWTService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException


@Service
class AuthorizationHelperService(private val jwtService: JWTService) {
    companion object {
        private const val BEARER = "Bearer "
    }

    fun shouldBeAdmin(authorizationHeader: String) {
        val payload = jwtService.decodeToken(readJWTToken(authorizationHeader))
        if (!payload.isAdmin) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed!"
            )
        }
    }

    fun shouldBeTheUser(authorizationHeader: String, username: String) {
        val payload = jwtService.decodeToken(readJWTToken(authorizationHeader))
        if (payload.username != username) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not allowed!"
            )
        }
    }

    fun readJWTToken(header: String): String {
        if (!header.contains(BEARER)) {
            return ""
        }
        return header.split(BEARER)[1]
    }
}