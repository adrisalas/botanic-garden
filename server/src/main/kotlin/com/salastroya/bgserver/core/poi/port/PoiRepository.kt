package com.salastroya.bgserver.core.poi.port

import com.salastroya.bgserver.core.poi.model.Poi
import kotlinx.coroutines.flow.Flow

interface PoiRepository {
    fun findAll(): Flow<Poi>
    suspend fun findById(id: Int): Poi?
    suspend fun existsById(id: Int): Boolean
    suspend fun insert(poi: Poi): Poi
    suspend fun update(poi: Poi): Poi
    suspend fun delete(id: Int)
}
