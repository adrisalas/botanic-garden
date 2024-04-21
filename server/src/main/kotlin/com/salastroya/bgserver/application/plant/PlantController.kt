package com.salastroya.bgserver.application.plant

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.model.Plant
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/plants")
class PlantController(
    private val useCases: PlantUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllPlants(): Flow<Plant> {
        return useCases.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findPlantById(@PathVariable id: Int): Plant {
        return useCases.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Plant with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertPlant(
        @RequestBody plant: Plant,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Plant {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (plant.id != null) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot provide id for creating a plant"
            )
        }
        return useCases.insert(plant)
    }

    @PutMapping("/{id}")
    suspend fun updatePlant(
        @PathVariable id: Int,
        @RequestBody plant: Plant,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Plant {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (plant.id != null && id != plant.id) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Mismatch between URI id and body id"
            )
        }
        return useCases.update(plant.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deletePlant(
        @PathVariable id: Int,
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