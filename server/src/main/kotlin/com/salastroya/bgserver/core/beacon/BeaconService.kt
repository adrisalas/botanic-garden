package com.salastroya.bgserver.core.beacon

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class BeaconService(private val repository: BeaconRepository) {
    fun findAll(): Flow<Beacon> {
        return repository.findAll()
    }

    suspend fun findById(id: String): Beacon? {
        return repository.findById(id)
    }

    suspend fun save(beacon: Beacon) {
        repository.save(beacon)
    }

    suspend fun delete(id: String) {
        repository.delete(id)
    }
}