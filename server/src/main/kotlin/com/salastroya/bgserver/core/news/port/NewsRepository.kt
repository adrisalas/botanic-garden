package com.salastroya.bgserver.core.news.port

import com.salastroya.bgserver.core.news.model.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun findAll(): Flow<News>
    suspend fun findById(id: Int): News?
    suspend fun existsById(id: Int): Boolean
    suspend fun insert(news: News): News
    suspend fun update(news: News): News
    suspend fun delete(id: Int)
}
