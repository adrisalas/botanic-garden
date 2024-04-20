package com.salastroya.bgserver.core.plant

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PlantUseCases(private val repository: PlantRepository) {

    fun findAll(): Flow<Plant> {
        return repository.findAll()
    }

    suspend fun findById(id: Int): Plant? {
        return repository.findById(id)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(plant: Plant): Plant {
        if (plant.id != null) {
            throw InvalidUseCaseException("Plant id cannot be provided for insert")
        }
        return repository.insert(plant)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun update(plant: Plant): Plant {
        if (plant.id == null) {
            throw InvalidUseCaseException("Plant id cannot be null for update")
        }
        if (!repository.existsById(plant.id)) {
            throw InvalidUseCaseException("Plant with id ${plant.id} does not exist")
        }
        return repository.update(plant)
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}