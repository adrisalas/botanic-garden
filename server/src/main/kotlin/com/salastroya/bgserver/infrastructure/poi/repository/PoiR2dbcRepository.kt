package com.salastroya.bgserver.infrastructure.news.repository

import com.salastroya.bgserver.infrastructure.poi.dto.PoiDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PoiR2dbcRepository : CoroutineCrudRepository<PoiDto, Int> {
    override fun findAll(): Flow<PoiDto>
    override suspend fun findById(id: Int): PoiDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun insert(poi: PoiDto): PoiDto
    suspend fun update(poi: PoiDto): PoiDto
    override suspend fun deleteById(id: Int)
}