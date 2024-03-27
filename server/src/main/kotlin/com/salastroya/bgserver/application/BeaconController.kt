package com.salastroya.bgserver.application

import com.salastroya.bgserver.core.beacon.Beacon
import com.salastroya.bgserver.core.beacon.BeaconService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/beacons")
class BeaconController(private val service: BeaconService) {

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
    suspend fun saveBeacon(@RequestBody beacon: Beacon): Beacon {
        service.save(beacon)
        return beacon
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteBeacon(@PathVariable id: String) {
        service.delete(id)
    }

    @ExceptionHandler(ResponseStatusException::class)
    suspend fun beaconExceptionHandler(ex: ResponseStatusException): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(ex.reason ?: "No reason specified")
        return ResponseEntity(error, ex.statusCode)
    }

    @ExceptionHandler(Exception::class)
    suspend fun unexpectedException(ex: Exception): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(ex.message ?: "")
        return ResponseEntity(error, INTERNAL_SERVER_ERROR)
    }
}