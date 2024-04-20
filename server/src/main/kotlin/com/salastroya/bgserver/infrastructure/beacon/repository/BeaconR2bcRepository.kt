package com.salastroya.bgserver.infrastructure.beacon.repository

import com.salastroya.bgserver.infrastructure.beacon.dto.BeaconDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BeaconR2bcRepository : CoroutineCrudRepository<BeaconDto, String> {
    override fun findAll(): Flow<BeaconDto>
    override suspend fun findById(id: String): BeaconDto?
    override suspend fun existsById(id: String): Boolean
    suspend fun insert(beacon: BeaconDto): BeaconDto
    suspend fun update(beacon: BeaconDto): BeaconDto
    override suspend fun deleteById(id: String)
}