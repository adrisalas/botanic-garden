package com.salastroya.bgserver.infrastructure.map.repository

import com.salastroya.bgserver.infrastructure.map.dto.RoutePointDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoutePointR2dbcRepository : CoroutineCrudRepository<RoutePointDto, Int> {
    fun findByRouteIdOrderByPosition(routeId: Int): Flow<RoutePointDto>
    fun findByPointId(pointId: Int): Flow<RoutePointDto>
    suspend fun insert(routePoint: RoutePointDto): RoutePointDto
    suspend fun deleteByRouteId(routeId: Int)
}