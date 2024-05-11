package com.salastroya.bgserver.application.telemetry

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.telemetry.TelemetryUseCases
import com.salastroya.bgserver.core.telemetry.model.PlantVisitCount
import com.salastroya.bgserver.core.telemetry.model.PoiVisitCount
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerDay
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerHour
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/telemetry")
class TelemetryController(
    private val useCases: TelemetryUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun setBeaconSeen(
        @RequestBody beaconSeenDto: BeaconSeenDto,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        val username = authHelper.extractUsername(authorizationHeader)
        val beaconId = beaconSeenDto.beaconId

        useCases.beaconSeenBy(beaconId, username)
    }

    @GetMapping("/most-visited-plants")
    fun mostVisitedPlants(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Flow<PlantVisitCount> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.mostVisitedPlants()
    }

    @GetMapping("/most-visited-pois")
    fun mostVisitedPois(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Flow<PoiVisitCount> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.mostVisitedPois()
    }

    @GetMapping("/visitors-per-hour")
    fun visitorsPerHour(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Flow<VisitorsPerHour> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.visitorsPerHour()
    }

    @GetMapping("/visitors-per-day")
    fun visitorsPerDay(
        @RequestHeader("Authorization") authorizationHeader: String
    ): Flow<VisitorsPerDay> {
        authHelper.shouldBeAdmin(authorizationHeader)

        return useCases.visitorsPerDay()
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