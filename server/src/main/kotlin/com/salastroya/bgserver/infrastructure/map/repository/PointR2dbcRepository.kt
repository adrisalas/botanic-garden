package com.salastroya.bgserver.infrastructure.map.repository

import com.salastroya.bgserver.infrastructure.map.dto.PointDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PointR2dbcRepository : CoroutineCrudRepository<PointDto, Int> {
    override fun findAll(): Flow<PointDto>
    override suspend fun findById(id: Int): PointDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun insert(point: PointDto): PointDto
    suspend fun update(point: PointDto): PointDto
    override suspend fun deleteById(id: Int)
}