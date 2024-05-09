package com.salastroya.bgserver.infrastructure.map

import com.salastroya.bgserver.core.map.model.Route
import com.salastroya.bgserver.core.map.repository.RouteRepository
import com.salastroya.bgserver.infrastructure.map.dto.RouteDto
import com.salastroya.bgserver.infrastructure.map.dto.RoutePointDto
import com.salastroya.bgserver.infrastructure.map.repository.RoutePointR2dbcRepository
import com.salastroya.bgserver.infrastructure.map.repository.RouteR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class RouteDataService(
    private val routeRepository: RouteR2dbcRepository,
    private val routePointRepository: RoutePointR2dbcRepository,
    private val pointDataService: PointDataService
) : RouteRepository {

    override fun findAllPublicRoutes(): Flow<Route> {
        return routeRepository.findAllByAssociatedUserIsNull()
            .map { it.toModel() }
    }

    private suspend fun RouteDto.toModel(): Route {
        val points = routePointRepository.findByRouteIdOrderByPosition(this.id ?: 0)
            .map {
                pointDataService.findById(it.pointId)
                    ?: throw RuntimeException("Inconsistency in DB, point ${it.pointId} does not exist")
            }

        return Route(
            id = this.id,
            name = this.name,
            points = points.toList()
        )
    }

    override suspend fun findById(id: Int): Route? {
        return routeRepository.findById(id)?.toModel()
    }

    override suspend fun findByAssociatedUser(username: String): Route? {
        return routeRepository.findByAssociatedUser(username)?.toModel()
    }

    override suspend fun insert(route: Route): Route {
        if (route.associatedUser != null) {
            routeRepository.findByAssociatedUser(route.associatedUser)
                ?.id
                ?.let {
                    routePointRepository.deleteByRouteId(it)
                    routeRepository.deleteById(it)
                }
        }
        val routeDto = routeRepository.insert(route.toRouteDto())

        if (routeDto.id == null) {
            throw RuntimeException("Unexpected error inserting data")
        }

        routePointRepository.deleteByRouteId(routeDto.id)

        route.points.forEachIndexed { index, it ->
            routePointRepository.insert(
                RoutePointDto(
                    routeId = routeDto.id,
                    pointId = it.id ?: throw RuntimeException("Unexpected error inserting data"),
                    position = index
                )
            )
        }

        return routeDto.toModel()
    }

    private fun Route.toRouteDto() = RouteDto(this.id, this.name, this.associatedUser)

    override suspend fun update(route: Route): Route {
        if (route.associatedUser != null) {
            throw RuntimeException(
                "System tried to update an user route, user routes are supposed to be re-generated from scratch."
            )
        }
        if (route.id == null) {
            throw RuntimeException("Route for update has id null")
        }

        routePointRepository.deleteByRouteId(route.id)

        route.points.forEachIndexed { index, it ->
            routePointRepository.insert(
                RoutePointDto(
                    routeId = route.id,
                    pointId = it.id ?: throw RuntimeException("Unexpected error inserting data"),
                    position = index
                )
            )
        }

        return routeRepository.update(route.toRouteDto()).toModel()
    }

    override suspend fun delete(id: Int) {
        routePointRepository.deleteByRouteId(id)
        routeRepository.deleteById(id)
    }

    override suspend fun deleteByPointId(id: Int) {
        routePointRepository.findByPointId(id)
            .map { it.routeId }
            .toList()
            .distinct()
            .forEach { delete(it) }
    }
}