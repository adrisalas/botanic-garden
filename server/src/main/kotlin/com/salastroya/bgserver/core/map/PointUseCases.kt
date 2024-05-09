package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.event.PointDeletedEvent
import com.salastroya.bgserver.core.map.event.PointUpdatedEvent
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.repository.PointRepository
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.event.PlantDeletedEvent
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.poi.event.PoiDeletedEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointUseCases(
    private val repository: PointRepository,
    private val plantUseCases: PlantUseCases,
    private val poiUseCases: PoiUseCases,
    private val publisher: ApplicationEventPublisher
) {

    fun findAll(): Flow<Point> {
        return repository.findAll()
    }

    suspend fun findById(id: Int): Point? {
        return repository.findById(id)
    }

    suspend fun existsById(id: Int): Boolean {
        return repository.existsById(id)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(point: Point): Point {
        if (point.id != null) {
            throw InvalidUseCaseException("Point id cannot be provided for insert")
        }
        point.items.forEach { checkItemIsValid(it) }

        return repository.insert(point)
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
    suspend fun update(point: Point): Point {
        if (point.id == null) {
            throw InvalidUseCaseException("Point id cannot be null for update")
        }
        if (!repository.existsById(point.id)) {
            throw InvalidUseCaseException("Point with id ${point.id} does not exist")
        }
        point.items.forEach { checkItemIsValid(it) }

        return repository.update(point)
            .also { publisher.publishEvent(PointUpdatedEvent(this, point.id)) }
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
            .also { publisher.publishEvent(PointDeletedEvent(this, id)) }
    }

    @EventListener(PlantDeletedEvent::class)
    fun handleEvent(event: PlantDeletedEvent) = runBlocking {
        val plantDeleted = Item(ItemType.PLANT, event.id)

        findAll()
            .filter { it.items.contains(plantDeleted) }
            .collect { point ->
                val itemsWithoutPlantDeleted = point.items.filter { it != plantDeleted }
                repository.update(
                    point.copy(items = itemsWithoutPlantDeleted)
                )
            }
    }

    @EventListener(PoiDeletedEvent::class)
    fun handleEvent(event: PoiDeletedEvent) = runBlocking {
        val poiDeleted = Item(ItemType.POI, event.id)

        findAll()
            .filter { it.items.contains(poiDeleted) }
            .collect { point ->
                val itemsWithoutPlantDeleted = point.items.filter { it != poiDeleted }
                repository.update(
                    point.copy(items = itemsWithoutPlantDeleted)
                )
            }
    }
}