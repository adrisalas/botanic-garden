package com.salastroya.bgserver.application.map

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.UserRouteUseCases
import com.salastroya.bgserver.core.map.command.RequestRouteCommand
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.Route
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.TimeoutCancellationException
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/map/my-route")
class UserRouteController(
    private val useCases: UserRouteUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    suspend fun findMyRoute(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Route {
        val username = authHelper.extractUsername(authorizationHeader)

        return useCases.findRouteOfUser(username)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "You have no route generated."
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun requestRoute(
        @RequestBody items: List<Item>,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Route {
        val username = authHelper.extractUsername(authorizationHeader)

        if (items.size < 2) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Select at least 2 items to generate a route. The first item will be the beginning of the route"
            )
        }

        try {
            return useCases.requestRoute(RequestRouteCommand(username, items))
        } catch (ex: TimeoutCancellationException) {
            throw ResponseStatusException(
                INTERNAL_SERVER_ERROR,
                "Timeout. This route was not generated in a reasonable time.",
                ex
            )
        }
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