package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.repository.BeaconRepository
import com.salastroya.bgserver.infrastructure.beacon.mapper.toDto
import com.salastroya.bgserver.infrastructure.beacon.mapper.toModel
import com.salastroya.bgserver.infrastructure.beacon.repository.BeaconR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class BeaconDataService(
    private val repository: BeaconR2dbcRepository
) : BeaconRepository {

    override fun findAll(): Flow<Beacon> {
        return repository
            .findAll()
            .map { it.toModel() }
    }


    override suspend fun findById(id: String): Beacon? {
        return repository.findById(id)?.toModel()
    }

    override suspend fun existsById(id: String): Boolean {
        return repository.existsById(id)
    }

    override suspend fun insert(beacon: Beacon): Beacon {
        return repository.insert(beacon.toDto()).toModel()
    }

    override suspend fun update(beacon: Beacon): Beacon {
        return repository.update(beacon.toDto()).toModel()
    }

    override suspend fun delete(id: String) {
        repository.deleteById(id)
    }
}