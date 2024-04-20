package com.salastroya.bgserver.infrastructure.news.repository

import com.salastroya.bgserver.infrastructure.news.dto.NewsDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NewsR2bcRepository : CoroutineCrudRepository<NewsDto, Int> {
    override fun findAll(): Flow<NewsDto>
    override suspend fun findById(id: Int): NewsDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun insert(news: NewsDto): NewsDto
    suspend fun update(news: NewsDto): NewsDto
    override suspend fun deleteById(id: Int)
}