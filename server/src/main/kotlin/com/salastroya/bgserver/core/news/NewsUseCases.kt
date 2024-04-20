package com.salastroya.bgserver.core.news

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.news.model.News
import com.salastroya.bgserver.core.news.port.NewsRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NewsUseCases(
    private val repository: NewsRepository,
) {

    fun findAll(): Flow<News> {
        return repository.findAll()
    }

    suspend fun findById(id: Int): News? {
        return repository.findById(id)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(news: News): News {
        if (news.id != null) {
            throw InvalidUseCaseException("News id cannot be provided for insert")
        }

        return repository.insert(news)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun update(news: News): News {
        if (news.id == null) {
            throw InvalidUseCaseException("News id cannot be null for update")
        }
        if (!repository.existsById(news.id)) {
            throw InvalidUseCaseException("News with id ${news.id} does not exist")
        }

        return repository.update(news)
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}