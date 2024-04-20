package com.salastroya.bgserver.core.beacon.repository

import com.salastroya.bgserver.core.beacon.model.Beacon
import kotlinx.coroutines.flow.Flow

interface BeaconRepository {
    fun findAll(): Flow<Beacon>
    suspend fun findById(id: String): Beacon?
    suspend fun existsById(id: String): Boolean
    suspend fun insert(beacon: Beacon): Beacon
    suspend fun update(beacon: Beacon): Beacon
    suspend fun delete(id: String)
}
