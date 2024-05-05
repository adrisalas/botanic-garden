package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.map.command.RequestRouteCommand
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.model.Route
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.coroutines.coroutineContext

private const val THIRTY_SECONDS_IN_MS = 30_000L

@Service
class UserRouteUseCases(
    private val routeUseCases: RouteUseCases,
    private val pathUseCases: PathUseCases
) {

    suspend fun findRouteOfUser(username: String): Route? {
        return routeUseCases.findByAssociatedUser(username)
    }

    @Transactional
    @Throws(TimeoutCancellationException::class)
    suspend fun requestRoute(command: RequestRouteCommand): Route {
        return routeUseCases.insertUsersRoute(
            Route(
                id = null,
                name = "Private route for user ${command.username}",
                points = generateBestRouteWith(command.items),
                associatedUser = command.username
            )
        )
    }

    @Throws(TimeoutCancellationException::class)
    private suspend fun generateBestRouteWith(itemsToVisit: List<Item>): List<Point> {
        val paths = pathUseCases.findAll().toList()
        val points = paths.flatMap { sequenceOf(it.pointA, it.pointB) }
            .distinctBy { it.id }

        val startPoint = points
            .find { point -> point.items.any { item -> itemsToVisit.contains(item) } }
            ?: throw RuntimeException("Requested items are not reachable.")

        return withTimeout(THIRTY_SECONDS_IN_MS) {
            findShortestRoute(
                startPoint,
                points,
                paths,
                itemsToVisit
            )
        }
    }

    /**
     * WARNING: Use it with a timeout mechanism.
     *
     *
     * This is a BFS algorithm applying weights while trying to search every item of the wanted items,
     * we cannot guarantee to find the route, and the algorithm will not stop.
     */
    private suspend fun findShortestRoute(
        startNode: Point,
        points: List<Point>,
        paths: List<Path>,
        itemsToVisit: List<Item>
    ): List<Point> {
        val idToPoint = points.associateBy { it.id!! }
        val adjacentPoints = generateAdjacentMap(paths)

        val nextRoutesToSearch = mutableListOf(
            RouteToExplore(listOf(startNode), itemsToVisit, 0.0)
        )

        while (coroutineContext.isActive) {
            delay(1) // Let other coroutines run
            nextRoutesToSearch.sortBy { it.meters }
            val currentRoute = nextRoutesToSearch.removeFirst()

            val lastPoint = currentRoute.route.last()

            val itemsNotFound = currentRoute.itemsNotFound.filterNot { lastPoint.items.contains(it) }
            if (itemsNotFound.isEmpty()) {
                return currentRoute.route
            }

            val secondToLastPointId = when (currentRoute.route.size) {
                0, 1 -> null
                else -> currentRoute.route[currentRoute.route.size - 2].id
            }

            adjacentPoints[lastPoint.id]?.forEach {
                val (id, meters) = it
                val wasItemFound = itemsNotFound.size != currentRoute.itemsNotFound.size
                val isNotTurningBack = id != secondToLastPointId

                if (wasItemFound || isNotTurningBack) {
                    nextRoutesToSearch.add(
                        RouteToExplore(
                            route = currentRoute.route + idToPoint[id]!!,
                            itemsNotFound = itemsNotFound,
                            meters = currentRoute.meters + meters
                        )
                    )
                }
            }

        }
        return emptyList()
    }

    private data class PointIdWithCost(val id: Int, val meters: Double)

    private data class RouteToExplore(
        val route: List<Point>,
        val itemsNotFound: List<Item>,
        val meters: Double
    )

    private suspend fun generateAdjacentMap(paths: List<Path>): Map<Int, List<PointIdWithCost>> {
        val adjacent = mutableMapOf<Int, MutableList<PointIdWithCost>>()

        paths.forEach { path ->
            val id1 = path.pointA.id!!
            val id2 = path.pointB.id!!
            adjacent.getOrPut(id1) {
                mutableListOf()
            }.add(
                PointIdWithCost(id2, path.meters)
            )

            adjacent.getOrPut(id2) {
                mutableListOf()
            }.add(
                PointIdWithCost(id1, path.meters)
            )
        }
        return adjacent
    }

    private fun List<Point>.hasAllItems(itemsToVisit: List<Item>): Boolean {
        val wasItemVisited = itemsToVisit.associateWith { false }.toMutableMap()

        this.forEach { point ->
            point.items.forEach { item ->
                wasItemVisited.computeIfPresent(item) { _, _ -> true }
            }
        }

        return wasItemVisited.values.all { it }
    }

}
