package com.salastroya.bgserver.infrastructure.beacon

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BeaconR2bcRepository : CoroutineCrudRepository<BeaconDto, String> {
    override suspend fun findById(id: String): BeaconDto?
    suspend fun insert(beacon: BeaconDto): BeaconDto
    override suspend fun deleteById(id: String)
}

//@Service
//class BeaconR2bcRepository {
//    suspend fun deleteById(id: String) {
//        delay(1000L)
//    }
//
//    fun findById(id: String): BeaconDto? {
//        return BeaconDto(id)
//    }
//
//}