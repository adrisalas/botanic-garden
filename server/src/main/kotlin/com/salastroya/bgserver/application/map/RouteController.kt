package com.salastroya.bgserver.application.map

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.RouteUseCases
import com.salastroya.bgserver.core.map.command.CreateRouteCommand
import com.salastroya.bgserver.core.map.model.Route
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/map/routes")
class RouteController(
    private val useCases: RouteUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllRoutes(): Flow<Route> {
        return useCases.findAllPublicRoutes()
    }

    @GetMapping("/{id}")
    suspend fun findRouteById(@PathVariable id: Int): Route {
        return useCases.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Route with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertRoute(
        @RequestBody command: CreateRouteCommand,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Route {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.insert(command)
    }

    @PutMapping("/{id}")
    suspend fun updateRoute(
        @PathVariable id: Int,
        @RequestBody command: CreateRouteCommand,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Route {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.update(id, command)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteRoute(
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