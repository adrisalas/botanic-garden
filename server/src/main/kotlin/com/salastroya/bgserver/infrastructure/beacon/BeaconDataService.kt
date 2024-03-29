package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.core.beacon.Beacon
import com.salastroya.bgserver.core.beacon.BeaconRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BeaconDataService(
    private val repository: BeaconR2bcRepository,
    private val beaconItemRepository: BeaconItemR2bcRepository
) : BeaconRepository {

    override fun findAll(): Flow<Beacon> {
        return repository
            .findAll()
            .map { Beacon(it.id, findItemId(it.id)) }
    }

    private suspend fun findItemId(beaconId: String): Long? {
        return beaconItemRepository.findById(beaconId)
            ?.itemId
    }

    override suspend fun findById(id: String): Beacon? {
        return repository.findById(id)?.let {
            Beacon(it.id, findItemId(it.id))
        }
    }

    @Transactional
    override suspend fun save(beacon: Beacon) {
        delete(beacon.id)

        repository.insert(BeaconDto(beacon.id))

        if (beacon.itemId != null) {
            beaconItemRepository.insert(BeaconItemDto(beacon.id, beacon.itemId))
        }

    }

    @Transactional
    override suspend fun delete(id: String) {
        beaconItemRepository.deleteById(id)
        repository.deleteById(id)
    }
}