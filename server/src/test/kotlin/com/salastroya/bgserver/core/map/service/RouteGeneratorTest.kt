package com.salastroya.bgserver.core.map.service

import com.salastroya.bgserver.core.map.PathUseCases
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.model.Point
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RouteGeneratorTest {
    companion object {
        val poi = Item(ItemType.POI, 1)
        val plant = Item(ItemType.PLANT, 1)
        val plant2 = Item(ItemType.PLANT, 2)
        val point1 = Point(1, 0.0, 0.0, listOf(poi))
        val point2 = Point(2, 0.0, 0.0, emptyList())
        val point3 = Point(3, 0.0, 0.0, emptyList())
        val point4 = Point(4, 0.0, 0.0, listOf(plant2))
        val point5 = Point(5, 0.0, 0.0, listOf(plant))
    }

    @MockK
    lateinit var pathUseCases: PathUseCases

    @InjectMockKs
    lateinit var routeGenerator: RouteGenerator

    @Test
    fun generateBestRoutePath1(): Unit = runBlocking {
        //   [1] - - - - - - - - -  [5]
        //    |                    / |
        //    |  /- - - -  - - - -/  |
        //    | /                    |
        //    2 - - - - - - - - - -  3
        coEvery { pathUseCases.findAll() }.returns(
            flowOf(
                Path(point1, point5, 500.0),
                Path(point1, point2, 10.0),
                Path(point3, point4, 10.0),
                Path(point4, point5, 10.0),
                Path(point2, point5, 10.0)
            )
        )

        val route = routeGenerator.generateBestRouteWith(listOf(poi, plant))

        assertThat(route).hasSize(3)
        assertThat(route[0].id).isEqualTo(1)
        assertThat(route[1].id).isEqualTo(2)
        assertThat(route[2].id).isEqualTo(5)
    }

    @Test
    fun generateBestRoutePath1Reverse(): Unit = runBlocking {
        //   [1] - - - - - - - - -  [5]
        //    |                    / |
        //    |  /- - - -  - - - -/  |
        //    | /                    |
        //    2 - - - - - - - - - -  3
        coEvery { pathUseCases.findAll() }.returns(
            flowOf(
                Path(point1, point5, 500.0),
                Path(point1, point2, 10.0),
                Path(point2, point3, 10.0),
                Path(point3, point5, 10.0),
                Path(point2, point5, 10.0)
            )
        )

        val route = routeGenerator.generateBestRouteWith(listOf(plant, poi))

        assertThat(route).hasSize(3)
        assertThat(route[0].id).isEqualTo(5)
        assertThat(route[1].id).isEqualTo(2)
        assertThat(route[2].id).isEqualTo(1)
    }

    @Test
    fun generateBestRouteFork(): Unit = runBlocking {
        coEvery { pathUseCases.findAll() }.returns(
            //     2 - - - - - - - - - - [1]
            //    / \
            //   /   \
            // [4]   [5]
            flowOf(
                Path(point1, point2, 500.0),
                Path(point2, point4, 10.0),
                Path(point2, point5, 10.0),
            )
        )

        val route = routeGenerator.generateBestRouteWith(listOf(poi, plant, plant2))

        assertThat(route).hasSize(5)
        assertThat(route[0].id).isEqualTo(1)
        assertThat(route[1].id).isEqualTo(2)
        assertThat(route[2].id).isEqualTo(4)
        assertThat(route[3].id).isEqualTo(2)
        assertThat(route[4].id).isEqualTo(5)
    }

    @Test
    fun generateBestRouteInfiniteLoop(): Unit = runBlocking {
        coEvery { pathUseCases.findAll() }.returns(
            // [1] - - - - - - - - - [5]
            //  |
            //  2 - 3
            //   \ /
            //    4
            flowOf(
                Path(point1, point2, 10.0),
                Path(point2, point3, 10.0),
                Path(point3, point4, 10.0),
                Path(point2, point4, 10.0),
                Path(point1, point5, 500.0),
            )
        )

        val route = routeGenerator.generateBestRouteWith(listOf(poi, plant))

        assertThat(route).hasSize(2)
        assertThat(route[0].id).isEqualTo(1)
        assertThat(route[1].id).isEqualTo(5)
    }

    @Test
    fun impossibleRoute(): Unit = runBlocking {
        coEvery { pathUseCases.findAll() }.returns(
            // [1]   [5]
            //  |     |
            //  2     4
            flowOf(
                Path(point1, point2, 10.0),
                Path(point4, point5, 10.0)
            )
        )

        assertThrows<NoSuchElementException> { routeGenerator.generateBestRouteWith(listOf(poi, plant)) }
    }

    @Test
    fun impossibleRouteNoItemFoundInAPath(): Unit = runBlocking {
        coEvery { pathUseCases.findAll() }.returns(flowOf())

        assertThrows<NoSuchElementException> { routeGenerator.generateBestRouteWith(listOf(poi, plant)) }
    }
}