package com.salastroya.bgserver.core.map.repository

import com.salastroya.bgserver.core.map.model.Path
import kotlinx.coroutines.flow.Flow

interface PathRepository {
    fun findAll(): Flow<Path>
    fun findAllContainingPointId(pointId: Int): Flow<Path>
    suspend fun insert(path: Path): Path
    suspend fun delete(pointAId: Int, pointBId: Int)
}