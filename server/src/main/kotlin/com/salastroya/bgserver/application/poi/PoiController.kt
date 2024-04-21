package com.salastroya.bgserver.application.poi

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.poi.model.Poi
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/poi")
class PoiController(
    private val useCases: PoiUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllPoi(): Flow<Poi> {
        return useCases.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findPoiById(@PathVariable id: Int): Poi {
        return useCases.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "Poi with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertPoi(
        @RequestBody poi: Poi,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Poi {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (poi.id != null) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot provide id for creating a poi"
            )
        }
        return useCases.insert(poi)
    }

    @PutMapping("/{id}")
    suspend fun updatePoi(
        @PathVariable id: Int,
        @RequestBody poi: Poi,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Poi {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (poi.id != null && id != poi.id) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Mismatch between URI id and body id"
            )
        }
        return useCases.update(poi.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deletePoi(
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