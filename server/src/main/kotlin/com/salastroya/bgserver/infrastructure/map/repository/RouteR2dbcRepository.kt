package com.salastroya.bgserver.infrastructure.map.repository

import com.salastroya.bgserver.infrastructure.map.dto.RouteDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RouteR2dbcRepository : CoroutineCrudRepository<RouteDto, Int> {
    fun findAllByAssociatedUserIsNull(): Flow<RouteDto>
    override suspend fun findById(id: Int): RouteDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun findByAssociatedUser(username: String): RouteDto?
    suspend fun insert(route: RouteDto): RouteDto
    suspend fun update(route: RouteDto): RouteDto
    override suspend fun deleteById(id: Int)
}