package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.command.CreatePathCommand
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.repository.PathRepository
import com.salastroya.bgserver.core.map.service.distanceBetweenTwoGeoPointsInMeters
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PathUseCases(
    private val repository: PathRepository,
    private val pointUseCases: PointUseCases
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
    }
}