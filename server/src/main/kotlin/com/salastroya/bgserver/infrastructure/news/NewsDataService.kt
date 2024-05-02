package com.salastroya.bgserver.infrastructure.news

import com.salastroya.bgserver.core.news.model.News
import com.salastroya.bgserver.core.news.port.NewsRepository
import com.salastroya.bgserver.infrastructure.news.mapper.toDto
import com.salastroya.bgserver.infrastructure.news.mapper.toModel
import com.salastroya.bgserver.infrastructure.news.repository.NewsR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class NewsDataService(
    private val repository: NewsR2dbcRepository
) : NewsRepository {

    override fun findAll(): Flow<News> {
        return repository.findAll()
            .map { it.toModel() }
    }


    override suspend fun findById(id: Int): News? {
        return repository.findById(id)?.toModel()
    }

    override suspend fun existsById(id: Int): Boolean {
        return repository.existsById(id)
    }

    override suspend fun insert(news: News): News {
        return repository.insert(news.toDto()).toModel()
    }

    override suspend fun update(news: News): News {
        return repository.update(news.toDto()).toModel()
    }

    override suspend fun delete(id: Int) {
        repository.deleteById(id)
    }
}