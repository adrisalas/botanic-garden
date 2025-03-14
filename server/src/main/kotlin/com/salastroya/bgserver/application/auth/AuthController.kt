package com.salastroya.bgserver.application.auth

import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.auth.AuthUseCases
import com.salastroya.bgserver.core.auth.command.ChangePasswordCommand
import com.salastroya.bgserver.core.auth.command.CreateUserCommand
import com.salastroya.bgserver.core.auth.command.UserLoginQuery
import com.salastroya.bgserver.core.auth.exception.UnauthorizedException
import com.salastroya.bgserver.core.auth.model.User
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val useCases: AuthUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/users")
    fun findAllUsers(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Flow<User> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.findAll()
    }

    @GetMapping("/users/{username}")
    suspend fun findUserByUsername(
        @PathVariable username: String,
        @RequestHeader("Authorization") authorizationHeader: String
    ): User {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.findByUsername(username)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "User with username: $username not found"
            )
    }

    @PostMapping("/users")
    @ResponseStatus(CREATED)
    suspend fun createUser(@RequestBody newUser: CreateUserCommand): User {
        return useCases.createUser(newUser)
    }

    @DeleteMapping("/users/{username}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteUser(
        @PathVariable username: String,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        useCases.delete(username)
    }

    @PostMapping("/users/{username}/password")
    suspend fun changePassword(
        @PathVariable username: String,
        @RequestBody changePasswordCommand: ChangePasswordCommand,
        @RequestHeader("Authorization") authorizationHeader: String
    ): User {
        authHelper.shouldBeTheUser(authorizationHeader, username)

        if (username != changePasswordCommand.username) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Mismatch between URI and body for field username"
            )
        }

        return useCases.updatePassword(changePasswordCommand)
    }

    @PostMapping("/login")
    suspend fun login(@RequestBody loginRequest: UserLoginQuery): TokenDto {
        return TokenDto(useCases.getLoginToken(loginRequest))
    }

    @ExceptionHandler(InvalidUseCaseException::class)
    @ResponseStatus(BAD_REQUEST)
    suspend fun badRequestHandler(ex: InvalidUseCaseException): ErrorMessage {
        log.warn { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(BAD_REQUEST)
    suspend fun serverWebInputException(ex: ServerWebInputException): ErrorMessage {
        ex.cause?.let {
            log.warn { it.message }
            return ErrorMessage(it.message ?: ex.message)
        }
        log.warn { ex.message }
        return ErrorMessage(ex.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(UNAUTHORIZED)
    suspend fun unauthorizedException(ex: UnauthorizedException): ErrorMessage {
        log.warn { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    suspend fun internalErrorHandler(ex: Throwable): ErrorMessage {
        log.warn { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(ResponseStatusException::class)
    suspend fun responseStatusExceptionHandler(ex: ResponseStatusException): ResponseEntity<ErrorMessage> {
        log.warn { ex.reason }
        val error = ErrorMessage(ex.reason ?: "No reason specified")
        return ResponseEntity(error, ex.statusCode)
    }
}