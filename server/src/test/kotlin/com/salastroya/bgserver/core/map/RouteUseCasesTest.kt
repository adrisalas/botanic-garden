package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.command.CreateRouteCommand
import com.salastroya.bgserver.core.map.event.PathDeletedEvent
import com.salastroya.bgserver.core.map.model.*
import com.salastroya.bgserver.core.map.repository.RouteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class RouteUseCasesTest {
    companion object {
        val ID = 101
        val NAME = "Shop"
        val ITEM = Item(ItemType.POI, 1)
        val POINT = Point(ID, 0.0, 0.0, listOf(ITEM))

        val ROUTE = Route(ID, NAME, listOf(POINT), null)

        val USERNAME = "username"
        val NAME_CUSTOM = "Private route for user $USERNAME"
        val ROUTE_CUSTOM = Route(ID, NAME_CUSTOM, listOf(POINT), USERNAME)
    }

    @MockK
    lateinit var repository: RouteRepository

    @MockK
    lateinit var pathUseCases: PathUseCases

    @MockK
    lateinit var pointUseCases: PointUseCases

    @InjectMockKs
    lateinit var useCases: RouteUseCases

    @Test
    fun findAllPublicRoutes(): Unit = runBlocking {
        every { repository.findAllPublicRoutes() }.returns(flowOf(ROUTE))

        val routes = useCases.findAllPublicRoutes().toList()

        assertThat(routes).hasSize(1)
        assertThat(routes[0].id).isEqualTo(ID)
        assertThat(routes[0].points).hasSize(1)
        assertThat(routes[0].points[0]).isEqualTo(POINT)
        assertThat(routes[0].name).isEqualTo(NAME)
        assertThat(routes[0].associatedUser).isNull()
    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(ROUTE)

        val route = useCases.findById(ID)

        assertThat(route).isNotNull()
        assertThat(route?.id).isEqualTo(ID)
        assertThat(route?.points).hasSize(1)
        assertThat(route?.points?.get(0)).isEqualTo(POINT)
        assertThat(route?.name).isEqualTo(NAME)
        assertThat(route?.associatedUser).isNull()
    }

    @Test
    fun findByAssociatedUser(): Unit = runBlocking {
        coEvery { repository.findByAssociatedUser(USERNAME) }.returns(ROUTE_CUSTOM)

        val route = useCases.findByAssociatedUser(USERNAME)

        assertThat(route).isNotNull()
        assertThat(route?.id).isEqualTo(ID)
        assertThat(route?.points).hasSize(1)
        assertThat(route?.points?.get(0)).isEqualTo(POINT)
        assertThat(route?.name).isEqualTo(NAME_CUSTOM)
        assertThat(route?.associatedUser).isEqualTo(USERNAME)
    }

    @Test
    fun insert(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { repository.insert(any()) }.returns(ROUTE)
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(POINT)

        useCases.insert(CreateRouteCommand(NAME, listOf(POINT.id!!)))

        coVerify { repository.insert(any()) }
    }

    @Test
    fun insertNoPathBetweenPoints(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { repository.insert(any()) }.returns(ROUTE)
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(POINT)

        assertThrows<InvalidUseCaseException> {
            useCases.insert(
                CreateRouteCommand(
                    NAME,
                    listOf(POINT.id!!, POINT.id!! + 1)
                )
            )
        }
    }

    @Test
    fun insertNoExistingPoint(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { repository.insert(any()) }.returns(ROUTE)
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(null)

        assertThrows<RuntimeException> {
            useCases.insert(
                CreateRouteCommand(
                    NAME,
                    listOf(POINT.id!!)
                )
            )
        }
    }

    @Test
    fun insertUsersRoute(): Unit = runBlocking {
        val routeCustomWithoutId = ROUTE_CUSTOM.copy(id = null)
        coEvery { repository.insert(routeCustomWithoutId) }.returns(ROUTE_CUSTOM)

        useCases.insertUsersRoute(routeCustomWithoutId)

        coVerify { repository.insert(routeCustomWithoutId) }
    }

    @Test
    fun insertUsersRouteWithID(): Unit = runBlocking {
        coEvery { repository.insert(ROUTE_CUSTOM) }.returns(ROUTE_CUSTOM)

        assertThrows<InvalidUseCaseException> { useCases.insertUsersRoute(ROUTE_CUSTOM) }
    }

    @Test
    fun update(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { repository.update(any()) }.returns(ROUTE)
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(POINT)

        useCases.update(ID, CreateRouteCommand(NAME, listOf(POINT.id!!)))

        coVerify { repository.update(any()) }
    }

    @Test
    fun updateNoPathBetweenPoints(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(POINT)

        assertThrows<InvalidUseCaseException> {
            useCases.update(
                ID,
                CreateRouteCommand(
                    NAME,
                    listOf(POINT.id!!, POINT.id!! + 1)
                )
            )
        }
    }

    @Test
    fun updateNoExistingPoint(): Unit = runBlocking {
        every { pathUseCases.findAll() }.returns(flowOf(Path(POINT, POINT, 10.0)))
        coEvery { pointUseCases.findById(POINT.id!!) }.returns(null)

        assertThrows<RuntimeException> {
            useCases.update(
                ID,
                CreateRouteCommand(
                    NAME,
                    listOf(POINT.id!!)
                )
            )
        }
    }

    @Test
    fun delete(): Unit = runBlocking {
        coEvery { repository.delete(ID) }.returns(Unit)

        useCases.delete(ID)

        coVerify { repository.delete(ID) }
    }

    @Test
    fun handleEventPathDeleted(): Unit = runBlocking {
        val listCaptor = mutableListOf<Int>()
        coEvery { repository.deleteByPointId(any()) }.returns(Unit)

        useCases.handleEvent(PathDeletedEvent(this, POINT.id!!, POINT.id!! + 1))

        coVerify(exactly = 2) { repository.deleteByPointId(capture(listCaptor)) }
        assertThat(listCaptor).hasSize(2)
        assertThat(listCaptor[0]).isEqualTo(POINT.id!!)
        assertThat(listCaptor[1]).isEqualTo(POINT.id!! + 1)
    }
}