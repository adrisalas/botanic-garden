package com.salastroya.bgserver.infrastructure.map.repository

import com.salastroya.bgserver.infrastructure.map.dto.PointItemDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PointItemR2dbcRepository : CoroutineCrudRepository<PointItemDto, Int> {
    fun findByPointId(id: Int): Flow<PointItemDto>
    suspend fun insert(pointItem: PointItemDto): PointItemDto
    suspend fun deleteByPointId(id: Int)
}