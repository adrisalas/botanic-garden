package com.salastroya.bgserver.application.beacon

import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.beacon.Beacon
import com.salastroya.bgserver.core.beacon.BeaconUseCases
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/beacons")
class BeaconController(private val service: BeaconUseCases) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllBeacons(): Flow<Beacon> {
        return service.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findBeaconById(@PathVariable id: String): Beacon {
        return service.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Beacon with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertBeacon(@RequestBody beacon: Beacon): Beacon {
        return service.insert(beacon)
    }

    @PutMapping
    suspend fun updateBeacon(@RequestBody beacon: Beacon): Beacon {
        return service.update(beacon)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteBeacon(@PathVariable id: String) {
        service.delete(id)
    }

    @ExceptionHandler(InvalidUseCaseException::class)
    @ResponseStatus(BAD_REQUEST)
    suspend fun badRequestHandler(ex: InvalidUseCaseException): ErrorMessage {
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    suspend fun internalErrorHandler(ex: Throwable): ErrorMessage {
        return ErrorMessage(ex.message ?: "")
    }

    @ExceptionHandler(ResponseStatusException::class)
    suspend fun responseStatusExceptionHandler(ex: ResponseStatusException): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(ex.reason ?: "No reason specified")
        return ResponseEntity(error, ex.statusCode)
    }
}