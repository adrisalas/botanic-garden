package com.salastroya.bgserver.infrastructure.map

import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.repository.PointRepository
import com.salastroya.bgserver.infrastructure.map.dto.PointDto
import com.salastroya.bgserver.infrastructure.map.mapper.toDto
import com.salastroya.bgserver.infrastructure.map.mapper.toModel
import com.salastroya.bgserver.infrastructure.map.mapper.toPointDto
import com.salastroya.bgserver.infrastructure.map.repository.PointItemR2dbcRepository
import com.salastroya.bgserver.infrastructure.map.repository.PointR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class PointDataService(
    private val pointRepository: PointR2dbcRepository,
    private val pointItemRepository: PointItemR2dbcRepository
) : PointRepository {

    override fun findAll(): Flow<Point> {
        return pointRepository.findAll()
            .map { it.toModel() }
    }

    private suspend fun PointDto.toModel(): Point {
        val items = pointItemRepository.findByPointId(this.id ?: 0).toModel()

        return Point(
            id = this.id,
            lat = this.lat,
            lon = this.lon,
            items = items.toList()
        )
    }

    override suspend fun existsById(id: Int): Boolean {
        return pointRepository.existsById(id)
    }

    override suspend fun findById(id: Int): Point? {
        return pointRepository.findById(id)?.toModel()
    }

    override suspend fun insert(point: Point): Point {
        val pointDto = pointRepository.insert(point.toPointDto())

        if (pointDto.id == null) {
            throw RuntimeException("Unexpected error inserting data")
        }

        pointItemRepository.deleteByPointId(pointDto.id)

        toDto(point.items, pointDto.id).forEach { pointItemRepository.insert(it) }

        return pointDto.toModel()
    }

    override suspend fun update(point: Point): Point {
        if (point.id == null) {
            throw RuntimeException("Point for update has id null")
        }

        pointItemRepository.deleteByPointId(point.id)

        toDto(point.items, point.id).forEach { pointItemRepository.insert(it) }

        return pointRepository.update(point.toPointDto()).toModel()
    }

    override suspend fun delete(id: Int) {
        pointItemRepository.deleteByPointId(id)
        pointRepository.deleteById(id)
    }
}
