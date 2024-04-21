package com.salastroya.bgserver.application.beacon

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.beacon.BeaconUseCases
import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/beacons")
class BeaconController(
    private val useCases: BeaconUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllBeacons(): Flow<Beacon> {
        return useCases.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findBeaconById(@PathVariable id: String): Beacon {
        return useCases.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Beacon with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertBeacon(
        @RequestBody beacon: Beacon,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Beacon {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.insert(beacon)
    }

    @PutMapping
    suspend fun updateBeacon(
        @RequestBody beacon: Beacon,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Beacon {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.update(beacon)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteBeacon(
        @PathVariable id: String,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        useCases.delete(id)
    }

    @ExceptionHandler(InvalidUseCaseException::class, JWTVerificationException::class)
    @ResponseStatus(BAD_REQUEST)
    suspend fun badRequestHandler(ex: RuntimeException): ErrorMessage {
        log.debug { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    suspend fun internalErrorHandler(ex: Throwable): ErrorMessage {
        log.debug { ex.message }
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(ResponseStatusException::class)
    suspend fun responseStatusExceptionHandler(ex: ResponseStatusException): ResponseEntity<ErrorMessage> {
        log.debug { ex.message }
        val error = ErrorMessage(ex.reason ?: "No reason specified")
        return ResponseEntity(error, ex.statusCode)
    }
}