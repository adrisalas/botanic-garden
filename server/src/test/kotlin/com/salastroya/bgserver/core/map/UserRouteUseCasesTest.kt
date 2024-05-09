package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.map.command.RequestRouteCommand
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType.POI
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.model.Route
import com.salastroya.bgserver.core.map.service.RouteGenerator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UserRouteUseCasesTest {
    companion object {
        val USERNAME = "username"
        val ID = 101
        val NAME = "Private route for user $USERNAME"
        val ITEM = Item(POI, 1)
        val POINT = Point(ID, 0.0, 0.0, listOf(ITEM))

        val ROUTE = Route(ID, NAME, listOf(POINT), USERNAME)
    }

    @MockK
    lateinit var routeGenerator: RouteGenerator

    @MockK
    lateinit var routeUseCases: RouteUseCases

    @InjectMockKs
    lateinit var useCases: UserRouteUseCases

    @Test
    fun findRouteOfUser(): Unit = runBlocking {
        coEvery { routeUseCases.findByAssociatedUser(USERNAME) }.returns(ROUTE)

        val route = useCases.findRouteOfUser(USERNAME)

        assertThat(route).isEqualTo(ROUTE)
    }

    @Test
    fun findRouteOfUserNoRouteFound(): Unit = runBlocking {
        coEvery { routeUseCases.findByAssociatedUser(USERNAME) }.returns(null)

        val route = useCases.findRouteOfUser(USERNAME)

        assertThat(route).isNull()
    }

    @Test
    fun requestRoute(): Unit = runBlocking {
        val itemsCaptor = slot<List<Item>>()
        val routeCaptor = slot<Route>()
        coEvery { routeUseCases.insertUsersRoute(any()) }.returns(ROUTE)
        coEvery { routeGenerator.generateBestRouteWith(any()) }.returns(listOf(POINT))

        useCases.requestRoute(RequestRouteCommand(USERNAME, listOf(ITEM)))

        coVerify { routeGenerator.generateBestRouteWith(capture(itemsCaptor)) }
        assertThat(itemsCaptor.captured).hasSize(1)
        assertThat(itemsCaptor.captured[0]).isEqualTo(ITEM)
        coVerify { routeUseCases.insertUsersRoute(capture(routeCaptor)) }
        assertThat(routeCaptor.captured.id).isNull()
        assertThat(routeCaptor.captured.associatedUser).isEqualTo(USERNAME)
        assertThat(routeCaptor.captured.name).isEqualTo(NAME)
        assertThat(routeCaptor.captured.points).hasSize(1)
        assertThat(routeCaptor.captured.points[0]).isEqualTo(POINT)
    }
}