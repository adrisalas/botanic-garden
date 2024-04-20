package com.salastroya.bgserver.core.plant.repository

import com.salastroya.bgserver.core.plant.model.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun findAll(): Flow<Plant>
    suspend fun existsById(id: Int): Boolean
    suspend fun findById(id: Int): Plant?
    suspend fun insert(plant: Plant): Plant
    suspend fun update(plant: Plant): Plant
    suspend fun delete(id: Int)
}