package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.command.CreatePathCommand
import com.salastroya.bgserver.core.map.event.PathDeletedEvent
import com.salastroya.bgserver.core.map.event.PointDeletedEvent
import com.salastroya.bgserver.core.map.event.PointUpdatedEvent
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.repository.PathRepository
import com.salastroya.bgserver.core.map.service.distanceBetweenTwoGeoPointsInMeters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PathUseCases(
    private val repository: PathRepository,
    private val pointUseCases: PointUseCases,
    private val publisher: ApplicationEventPublisher
) {
    fun findAll(): Flow<Path> {
        return repository.findAll()
    }

    fun findAllByPointId(pointId: Int): Flow<Path> {
        return repository.findAllContainingPointId(pointId)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(command: CreatePathCommand): Path {
        val points = naturalOrdered(command.pointAId, command.pointBId)

        val pointA = pointUseCases.findById(points.first)
            ?: throw InvalidUseCaseException("Associated point with id ${points.first} does not exist")
        val pointB = pointUseCases.findById(points.second)
            ?: throw InvalidUseCaseException("Associated point with id ${points.second} does not exist")

        val path = Path(
            pointA,
            pointB,
            distanceBetweenTwoGeoPointsInMeters(pointA, pointB)
        )

        return repository.insert(path)
    }

    private fun naturalOrdered(a: Int, b: Int): Pair<Int, Int> {
        val twoNumbers = listOf(a, b).sorted()
        return Pair(twoNumbers[0], twoNumbers[1])
    }

    @Transactional
    suspend fun delete(pointAId: Int, pointBId: Int) {
        val points = naturalOrdered(pointAId, pointBId)

        repository.delete(points.first, points.second)
            .also { publisher.publishEvent(PathDeletedEvent(this, points.first, points.second)) }
    }

    @EventListener(PointUpdatedEvent::class)
    fun handleEvent(event: PointUpdatedEvent) = runBlocking {
        repository.findAllContainingPointId(event.id)
            .toList()
            .forEach {
                repository.update(
                    it.copy(meters = distanceBetweenTwoGeoPointsInMeters(it.pointA, it.pointB))
                )
            }
    }

    @EventListener(PointDeletedEvent::class)
    fun handleEvent(event: PointDeletedEvent) = runBlocking {
        // Notice Spring Boot Events are SYNCHRONOUS
        // This code will yield an error if it was ASYNCHRONOUS
        repository.findAllContainingPointId(event.id)
            .toList()
            .forEach { delete(it.pointA.id!!, it.pointB.id!!) }
    }
}