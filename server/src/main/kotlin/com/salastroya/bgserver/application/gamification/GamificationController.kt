package com.salastroya.bgserver.application.gamification

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.auth.AuthUseCases
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.gamification.FindPlantGameUseCases
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/gamification/")
class GamificationController(
    private val authUseCases: AuthUseCases,
    private val findPlantGame: FindPlantGameUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping("/my-points")
    suspend fun getMyPoints(@RequestHeader("Authorization") authorizationHeader: String): PointsDto {
        val username = authHelper.extractUsername(authorizationHeader)

        return getPointsForUser(username)
    }

    private suspend fun getPointsForUser(username: String): PointsDto {
        return PointsDto(
            username,
            findPlantGame.getUserPoints(username)
        )
    }

    @GetMapping("/all-users")
    suspend fun getAllUsersPoints(@RequestHeader("Authorization") authorizationHeader: String): Flow<PointsDto> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return authUseCases.findAll().map { getPointsForUser(it.username) }
    }

    @ExceptionHandler(InvalidUseCaseException::class, JWTVerificationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    suspend fun badRequestHandler(ex: RuntimeException): ErrorMessage {
        log.warn { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    suspend fun serverWebInputException(ex: ServerWebInputException): ErrorMessage {
        ex.cause?.let {
            log.warn { it.message }
            return ErrorMessage(it.message ?: ex.message)
        }
        log.warn { ex.message }
        return ErrorMessage(ex.message)
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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