package com.salastroya.bgserver.infrastructure.plant

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlantR2bcRepository : CoroutineCrudRepository<PlantDto, Int> {
    override suspend fun findById(id: Int): PlantDto?
    override suspend fun existsById(id: Int): Boolean
    suspend fun insert(beacon: PlantDto): PlantDto
    suspend fun update(beacon: PlantDto): PlantDto
    override suspend fun deleteById(id: Int)
}