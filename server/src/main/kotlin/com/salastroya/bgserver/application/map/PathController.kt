package com.salastroya.bgserver.application.map

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.PathUseCases
import com.salastroya.bgserver.core.map.command.CreatePathCommand
import com.salastroya.bgserver.core.map.model.Path
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/api/map/paths")
class PathController(
    private val useCases: PathUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllPaths(): Flow<Path> {
        return useCases.findAll()
    }

    @GetMapping(params = ["pointId"])
    suspend fun findAllPathsOfPointId(@RequestParam pointId: Int): Flow<Path> {
        return useCases.findAllByPointId(pointId)
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertPath(
        @RequestBody createPathCommand: CreatePathCommand,
        @RequestHeader("Authorization") authorizationHeader: String
    ): Path {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (createPathCommand.pointAId == createPathCommand.pointBId) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot create a path between the same point"
            )
        }

        return useCases.insert(createPathCommand)
    }

    @DeleteMapping("/{pointAId}/{pointBId}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deletePath(
        @PathVariable pointAId: Int,
        @PathVariable pointBId: Int,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (pointAId == pointBId) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot delete a path between the same point"
            )
        }

        useCases.delete(pointAId, pointBId)
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
        log.warn { ex.message }
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