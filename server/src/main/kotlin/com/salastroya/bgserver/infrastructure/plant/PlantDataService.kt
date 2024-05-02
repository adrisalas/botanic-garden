package com.salastroya.bgserver.infrastructure.plant

import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.repository.PlantRepository
import com.salastroya.bgserver.infrastructure.plant.mapper.toDto
import com.salastroya.bgserver.infrastructure.plant.mapper.toModel
import com.salastroya.bgserver.infrastructure.plant.repository.PlantR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class PlantDataService(
    private val repository: PlantR2dbcRepository,
) : PlantRepository {

    override fun findAll(): Flow<Plant> {
        return repository
            .findAll()
            .map { it.toModel() }
    }

    override suspend fun existsById(id: Int): Boolean {
        return repository.existsById(id)
    }

    override suspend fun findById(id: Int): Plant? {
        return repository.findById(id)?.toModel()
    }

    override suspend fun insert(plant: Plant): Plant {
        return repository.insert(plant.toDto()).toModel()
    }

    override suspend fun update(plant: Plant): Plant {
        return repository.update(plant.toDto()).toModel()
    }

    override suspend fun delete(id: Int) {
        repository.deleteById(id)
    }
}
