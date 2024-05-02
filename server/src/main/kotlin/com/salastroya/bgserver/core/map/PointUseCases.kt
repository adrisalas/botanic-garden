package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.repository.PointRepository
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.poi.PoiUseCases
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointUseCases(
    private val repository: PointRepository,
    private val plantUseCases: PlantUseCases,
    private val poiUseCases: PoiUseCases
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
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
    }
}