package com.salastroya.bgserver.core.beacon

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.PlantRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BeaconUseCases(
    private val repository: BeaconRepository,
    private val plantRepository: PlantRepository
) {
    
    fun findAll(): Flow<Beacon> {
        return repository.findAll()
    }

    suspend fun findById(id: String): Beacon? {
        return repository.findById(id)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(beacon: Beacon): Beacon {
        if (repository.existsById(beacon.id)) {
            throw InvalidUseCaseException("Beacon with id ${beacon.id} already exists")
        }
        checkItemIsValid(beacon.item)

        return repository.insert(beacon)
    }

    private suspend fun checkItemIsValid(item: Item?) {
        if (item != null && !isItemExisting(item)) {
            throw InvalidUseCaseException(
                "Associated item of type ${item.type} with id ${item.id} does not exist"
            )
        }
    }

    private suspend fun isItemExisting(item: Item): Boolean {
        return when (item.type) {
            ItemType.PLANT -> plantRepository.existsById(item.id)
            ItemType.LOCATION -> TODO()
        }
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun update(beacon: Beacon): Beacon {
        if (!repository.existsById(beacon.id)) {
            throw InvalidUseCaseException("Beacon with id ${beacon.id} does not exist")
        }
        checkItemIsValid(beacon.item)

        return repository.update(beacon)
    }

    @Transactional
    suspend fun delete(id: String) {
        repository.delete(id)
    }
}