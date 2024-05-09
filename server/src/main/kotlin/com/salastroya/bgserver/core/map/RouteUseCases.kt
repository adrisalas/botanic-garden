package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.command.CreateRouteCommand
import com.salastroya.bgserver.core.map.event.PathDeletedEvent
import com.salastroya.bgserver.core.map.model.Route
import com.salastroya.bgserver.core.map.repository.RouteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RouteUseCases(
    private val repository: RouteRepository,
    private val pointUseCases: PointUseCases,
    private val pathUseCases: PathUseCases
) {
    fun findAllPublicRoutes(): Flow<Route> {
        return repository.findAllPublicRoutes()
    }

    suspend fun findById(id: Int): Route? {
        return repository.findById(id)
    }

    suspend fun findByAssociatedUser(username: String): Route? {
        return repository.findByAssociatedUser(username)
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insert(command: CreateRouteCommand): Route {
        checkRouteIsValid(command)

        val points = command.points.map {
            pointUseCases.findById(it)
                ?: throw RuntimeException("Unexpected error getting point $it")
        }

        return repository.insert(Route(null, command.name, points))
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun insertUsersRoute(route: Route): Route {
        return repository.insert(route)
    }

    private suspend fun checkRouteIsValid(command: CreateRouteCommand) {
        val paths = pathUseCases.findAll().toList()

        command.points.windowed(2)
            .map { it.sorted() } // Paths are sorted by id
            .forEach {
                val firstPointId = it[0]
                val secondPointId = it[1]

                val notConnected = paths.none { path ->
                    path.pointA.id == firstPointId
                            && path.pointB.id == secondPointId
                }
                if (notConnected) {
                    throw InvalidUseCaseException("Point $firstPointId and point $secondPointId are not connected.")
                }
            }
    }

    @Transactional
    @Throws(InvalidUseCaseException::class)
    suspend fun update(id: Int, command: CreateRouteCommand): Route {
        checkRouteIsValid(command)

        val points = command.points.map {
            pointUseCases.findById(it)
                ?: throw RuntimeException("Unexpected error getting point $it")
        }

        return repository.update(Route(id, command.name, points))
    }

    @Transactional
    suspend fun delete(id: Int) {
        repository.delete(id)
    }

    @EventListener(PathDeletedEvent::class)
    fun handleEvent(event: PathDeletedEvent) = runBlocking {
        repository.deleteByPointId(event.pointAId)
        repository.deleteByPointId(event.pointBId)
    }
}