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

private const val TEN_SECONDS_IN_MS = 10_000L

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

        return withTimeout(TEN_SECONDS_IN_MS) {
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
        nodes: List<Point>,
        edges: List<Path>,
        itemsToVisit: List<Item>
    ): List<Point> {
        val idToNode = nodes.associateBy { it.id!! }
        val adjacentNodes = generateAdjacentMap(edges)

        val nextRoutesToSearch = mutableListOf(listOf(startNode) to 0.0)

        while (coroutineContext.isActive) {
            delay(1) // Let other coroutines run
            nextRoutesToSearch.sortBy { it.second }
            val currentRoute = nextRoutesToSearch.removeFirst()

            if (currentRoute.first.hasAllItems(itemsToVisit)) {
                return currentRoute.first
            }

            val lastPoint = currentRoute.first.last()

            adjacentNodes[lastPoint.id]?.forEach {
                val newRoute = currentRoute.first + idToNode[it.first]!!
                val cost = currentRoute.second + it.second
                nextRoutesToSearch.add(newRoute to cost)
            }

        }
        return emptyList()
    }

    private suspend fun generateAdjacentMap(paths: List<Path>): Map<Int, List<Pair<Int, Double>>> {
        val adjacent = mutableMapOf<Int, MutableList<Pair<Int, Double>>>()

        paths.forEach { path ->
            val p1 = path.pointA.id!!
            val p2 = path.pointB.id!!
            adjacent.getOrPut(p1) {
                mutableListOf()
            }.add(p2 to path.meters)

            adjacent.getOrPut(p2) {
                mutableListOf()
            }.add(p1 to path.meters)
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
