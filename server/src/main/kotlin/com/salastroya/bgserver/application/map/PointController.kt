package com.salastroya.bgserver.application.map

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.PointUseCases
import com.salastroya.bgserver.core.map.model.Point
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/map/points")
class PointController(
    private val useCases: PointUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllPoints(): Flow<Point> {
        return useCases.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findPointById(@PathVariable id: Int): Point {
        return useCases.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Point with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertPoint(
        @RequestBody point: Point,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Point {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (point.id != null) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot provide id for creating a point"
            )
        }
        return useCases.insert(point)
    }

    @PutMapping("/{id}")
    suspend fun updatePoint(
        @PathVariable id: Int,
        @RequestBody point: Point,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Point {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (point.id != null && id != point.id) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Mismatch between URI id and body id"
            )
        }
        return useCases.update(point.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deletePoint(
        @PathVariable id: Int,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        useCases.delete(id)
    }

    @ExceptionHandler(InvalidUseCaseException::class, JWTVerificationException::class)
    @ResponseStatus(BAD_REQUEST)
    suspend fun badRequestHandler(ex: RuntimeException): ErrorMessage {
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