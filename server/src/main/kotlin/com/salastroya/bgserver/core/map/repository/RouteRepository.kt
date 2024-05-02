package com.salastroya.bgserver.core.map.repository

import com.salastroya.bgserver.core.map.model.Route
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun findAllPublicRoutes(): Flow<Route>
    suspend fun findById(id: Int): Route?
    suspend fun findByAssociatedUser(username: String): Route?
    suspend fun insert(route: Route): Route
    suspend fun update(route: Route): Route
    suspend fun delete(id: Int)
}