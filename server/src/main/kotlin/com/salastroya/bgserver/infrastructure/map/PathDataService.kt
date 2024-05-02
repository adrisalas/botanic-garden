package com.salastroya.bgserver.infrastructure.map

import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.repository.PathRepository
import com.salastroya.bgserver.infrastructure.map.dto.PathDto
import com.salastroya.bgserver.infrastructure.map.repository.PathR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class PathDataService(
    private val repository: PathR2dbcRepository,
    private val pointDataService: PointDataService
) : PathRepository {
    override fun findAll(): Flow<Path> {
        return repository.findAll()
            .map { it.toModel() }
    }

    suspend fun PathDto.toModel(): Path {
        return Path(
            pointA = pointDataService.findById(this.pointAId)
                ?: throw RuntimeException("Inconsistency in DB, point ${this.pointAId} does not exist"),
            pointB = pointDataService.findById(this.pointBId)
                ?: throw RuntimeException("Inconsistency in DB, point ${this.pointBId} does not exist"),
            meters = this.meters
        )
    }

    override fun findAllContainingPointId(pointId: Int): Flow<Path> {
        return repository.findAllByPointId(pointId)
            .map { it.toModel() }
    }

    override suspend fun insert(path: Path): Path {
        return repository.insert(path.toDto()).toModel()
    }

    suspend fun Path.toDto(): PathDto {
        return PathDto(
            pointAId = this.pointA.id
                ?: throw RuntimeException("Internal error, creating a path with id null"),
            pointBId = this.pointB.id
                ?: throw RuntimeException("Internal error, creating a path with id null"),
            meters = meters
        )
    }

    override suspend fun delete(pointAId: Int, pointBId: Int) {
        repository.deleteByPointAIdAndPointBId(pointAId, pointBId)
    }

}
