package com.salastroya.bgserver.application.gamification

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.gamification.FindPlantGameUseCases
import com.salastroya.bgserver.core.plant.model.Plant
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/gamification/find-plant")
class FindPlantGameController(
    private val useCases: FindPlantGameUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    suspend fun getPlantToBeFound(): Plant {
        return useCases.getPlantToBeFound()
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "There is no active game at the moment"
            )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun setNewPlant(
        @RequestBody plantDto: PlantDto,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        useCases.setPlant(plantDto.plantId)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deactivate(
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        useCases.deactivateLastGame()
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