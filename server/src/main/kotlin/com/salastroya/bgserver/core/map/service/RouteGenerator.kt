package com.salastroya.bgserver.core.map.service

import com.salastroya.bgserver.core.map.PathUseCases
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.model.Point
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Service
import kotlin.coroutines.coroutineContext

private const val THIRTY_SECONDS_IN_MS = 30_000L

@Service
class RouteGenerator(private val pathUseCases: PathUseCases) {

    @Throws(TimeoutCancellationException::class)
    suspend fun generateBestRouteWith(itemsToVisit: List<Item>): List<Point> {
        val paths = pathUseCases.findAll().toList()
        val points = paths.flatMap { sequenceOf(it.pointA, it.pointB) }
            .distinctBy { it.id }

        val startPoint = points
            .find { point -> point.items.any { item -> itemsToVisit[0] == item } }
            ?: throw NoSuchElementException("Requested items are not reachable.")

        return withTimeout(THIRTY_SECONDS_IN_MS) {
            findShortestRoute(startPoint, points, paths, itemsToVisit)
        }
    }

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

            adjacentPoints[lastPoint.id]?.forEach { pointIdWithCost ->
                val nextPoint = idToPoint[pointIdWithCost.id]!!
                val cost = pointIdWithCost.meters

                val timesNextPointWasCrossed = currentRoute.route.count { it.id == nextPoint.id }
                val degree = adjacentPoints[nextPoint.id]!!.size
                val degreeWasNotExceeded = timesNextPointWasCrossed < degree

                val wasItemFound = itemsNotFound.size != currentRoute.itemsNotFound.size
                val isNotTurningBack = nextPoint.id != secondToLastPointId

                if ((wasItemFound || isNotTurningBack) && degreeWasNotExceeded) {
                    nextRoutesToSearch.add(
                        RouteToExplore(
                            route = currentRoute.route + nextPoint,
                            itemsNotFound = itemsNotFound,
                            meters = currentRoute.meters + cost
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
}