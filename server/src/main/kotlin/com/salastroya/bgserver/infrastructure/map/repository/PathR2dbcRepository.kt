package com.salastroya.bgserver.infrastructure.map.repository

import com.salastroya.bgserver.infrastructure.map.dto.PathDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PathR2dbcRepository : CoroutineCrudRepository<PathDto, Int> {
    override fun findAll(): Flow<PathDto>

    @Query("select * from map_path p WHERE p.point_a_id = :pointId OR p.point_b_id = :pointId")
    fun findAllByPointId(pointId: Int): Flow<PathDto>
    suspend fun insert(path: PathDto): PathDto
    suspend fun deleteByPointAIdAndPointBId(pointAId: Int, pointBId: Int)
}