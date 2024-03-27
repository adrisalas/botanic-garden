package com.salastroya.bgserver.core.beacon

import kotlinx.coroutines.flow.Flow

interface BeaconRepository {
    fun findAll(): Flow<Beacon>
    suspend fun findById(id: String): Beacon?
    suspend fun save(beacon: Beacon)
    suspend fun delete(id: String)
}
