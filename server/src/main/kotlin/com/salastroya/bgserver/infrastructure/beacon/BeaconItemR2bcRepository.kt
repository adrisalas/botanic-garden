package com.salastroya.bgserver.infrastructure.beacon

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BeaconItemR2bcRepository : CoroutineCrudRepository<BeaconItemDto, String> {
    override suspend fun findById(id: String): BeaconItemDto?
    suspend fun insert(beacon: BeaconItemDto): BeaconItemDto
    override suspend fun deleteById(id: String)
}

//@Service
//class BeaconItemR2bcRepository {
//    suspend fun deleteById(id: String) {
//        delay(1000L)
//    }
//
//    suspend fun findByBeaconId(beaconId: String): BeaconItemDto? {
//        delay(1000L)
//        return BeaconItemDto(beaconId, 1L)
//    }
//
//}
