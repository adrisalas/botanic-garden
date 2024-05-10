package com.salastroya.bgserver.core.beacon

import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.model.Item
import com.salastroya.bgserver.core.beacon.model.ItemType
import com.salastroya.bgserver.core.beacon.repository.BeaconRepository
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.event.PlantDeletedEvent
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.poi.event.PoiDeletedEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BeaconUseCases(
    private val repository: BeaconRepository,
    private val plantUseCases: PlantUseCases,
    private val poiUseCases: PoiUseCases
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
            ItemType.PLANT -> plantUseCases.existsById(item.id)
            ItemType.POI -> poiUseCases.existsById(item.id)
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

    @EventListener(PlantDeletedEvent::class)
    fun handleEvent(event: PlantDeletedEvent) = runBlocking {
        findAll()
            .filter { it.item == Item(ItemType.PLANT, event.id) }
            .collect { repository.update(it.copy(item = null)) }
    }

    @EventListener(PoiDeletedEvent::class)
    fun handleEvent(event: PoiDeletedEvent) = runBlocking {
        findAll()
            .filter { it.item == Item(ItemType.POI, event.id) }
            .collect { repository.update(it.copy(item = null)) }
    }
}