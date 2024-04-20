package com.salastroya.bgserver.infrastructure.plant.repository

import com.salastroya.bgserver.infrastructure.plant.dto.PlantDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlantR2bcRepository : CoroutineCrudRepository<PlantDto, Int> {
    override fun findAll(): Flow<PlantDto>
    override suspend fun findById(id: Int): PlantDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun insert(beacon: PlantDto): PlantDto
    suspend fun update(beacon: PlantDto): PlantDto
    override suspend fun deleteById(id: Int)
}