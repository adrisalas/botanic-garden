package com.salastroya.bgserver.application.news

import com.auth0.jwt.exceptions.JWTVerificationException
import com.salastroya.bgserver.application.AuthorizationHelperService
import com.salastroya.bgserver.application.ErrorMessage
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.news.NewsUseCases
import com.salastroya.bgserver.core.news.model.News
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/news")
class NewsController(
    private val service: NewsUseCases,
    private val authHelper: AuthorizationHelperService
) {

    private val log = KotlinLogging.logger {}

    @GetMapping
    fun findAllNews(): Flow<News> {
        return service.findAll()
    }

    @GetMapping("/{id}")
    suspend fun findNewsById(@PathVariable id: Int): News {
        return service.findById(id)
            ?: throw ResponseStatusException(
                NOT_FOUND,
                "News with id: $id not found"
            )
    }

    @PostMapping
    @ResponseStatus(CREATED)
    suspend fun insertNews(
        @RequestBody news: News,
        @RequestHeader("Authorization") authorizationHeader: String
    ): News {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (news.id != null) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "You cannot provide id for creating a news"
            )
        }
        return service.insert(news)
    }

    @PutMapping("/{id}")
    suspend fun updateNews(
        @PathVariable id: Int,
        @RequestBody news: News,
        @RequestHeader("Authorization") authorizationHeader: String
    ): News {
        authHelper.shouldBeAdmin(authorizationHeader)

        if (news.id != null && id != news.id) {
            throw ResponseStatusException(
                BAD_REQUEST,
                "Mismatch between URI id and body id"
            )
        }
        return service.update(news.copy(id = id))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    suspend fun deleteNews(
        @PathVariable id: Int,
        @RequestHeader("Authorization") authorizationHeader: String
    ) {
        authHelper.shouldBeAdmin(authorizationHeader)

        service.delete(id)
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