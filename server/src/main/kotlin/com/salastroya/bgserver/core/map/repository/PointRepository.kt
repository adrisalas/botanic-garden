package com.salastroya.bgserver.core.map.repository

import com.salastroya.bgserver.core.map.model.Point
import kotlinx.coroutines.flow.Flow

interface PointRepository {
    fun findAll(): Flow<Point>
    suspend fun existsById(id: Int): Boolean
    suspend fun findById(id: Int): Point?
    suspend fun insert(point: Point): Point
    suspend fun update(point: Point): Point
    suspend fun delete(id: Int)
}