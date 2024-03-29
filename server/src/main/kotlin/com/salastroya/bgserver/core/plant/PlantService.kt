package com.salastroya.bgserver.core.plant

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class PlantService(private val repository: PlantRepository) {
    fun findAll(): Flow<Plant> {
        return repository.findAll()
    }

    suspend fun findById(id: Int): Plant? {
        return repository.findById(id)
    }

    suspend fun insert(plant: Plant): Plant {
        if (plant.id != null) {
            throw RuntimeException("Plant id cannot be provided for insert")
        }
        return repository.insert(plant)
    }

    suspend fun update(plant: Plant): Plant {
        if (plant.id == null) {
            throw RuntimeException("Plant id cannot be null for update")
        }
        if (!repository.existsById(plant.id)) {
            throw RuntimeException("Cannot update plant that already exists")
        }
        return repository.update(plant)
    }

    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}