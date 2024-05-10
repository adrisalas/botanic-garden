package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.command.CreatePathCommand
import com.salastroya.bgserver.core.map.event.PathDeletedEvent
import com.salastroya.bgserver.core.map.event.PointDeletedEvent
import com.salastroya.bgserver.core.map.event.PointUpdatedEvent
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType
import com.salastroya.bgserver.core.map.model.Path
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.repository.PathRepository
import io.mockk.*
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
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class PathUseCasesTest {
    companion object {
        val ITEM = Item(ItemType.POI, 1)
        val POINT_A = Point(1, 0.0, 0.0, listOf())
        val POINT_B = Point(2, 0.00001, 0.00001, listOf(ITEM))
        val METERS = 1.0
        val PATH = Path(POINT_A, POINT_B, METERS)
    }

    @MockK
    lateinit var repository: PathRepository

    @MockK
    lateinit var pointUseCases: PointUseCases

    @MockK
    lateinit var publisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var useCases: PathUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(PATH))

        val paths = useCases.findAll().toList()

        assertThat(paths).hasSize(1)
        assertThat(paths[0].pointA).isEqualTo(POINT_A)
        assertThat(paths[0].pointB).isEqualTo(POINT_B)
        assertThat(paths[0].meters).isEqualTo(METERS)
    }

    @Test
    fun findAllByPointId(): Unit = runBlocking {
        every { repository.findAllContainingPointId(1) }.returns(flowOf(PATH))

        val paths = useCases.findAllByPointId(1).toList()

        assertThat(paths).hasSize(1)
        assertThat(paths[0].pointA).isEqualTo(POINT_A)
        assertThat(paths[0].pointB).isEqualTo(POINT_B)
        assertThat(paths[0].meters).isEqualTo(METERS)
    }

    @Test
    fun insert(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(PATH)
        coEvery { pointUseCases.findById(POINT_A.id!!) }.returns(POINT_A)
        coEvery { pointUseCases.findById(POINT_B.id!!) }.returns(POINT_B)

        useCases.insert(CreatePathCommand(POINT_A.id!!, POINT_B.id!!))

        coVerify { repository.insert(any()) }
    }

    @Test
    fun insertPointADoesNotExists(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(PATH)
        coEvery { pointUseCases.findById(POINT_A.id!!) }.returns(null)
        coEvery { pointUseCases.findById(POINT_B.id!!) }.returns(POINT_B)

        assertThrows<InvalidUseCaseException> { useCases.insert(CreatePathCommand(POINT_A.id!!, POINT_B.id!!)) }
    }

    @Test
    fun insertPointBDoesNotExists(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(PATH)
        coEvery { pointUseCases.findById(POINT_A.id!!) }.returns(POINT_A)
        coEvery { pointUseCases.findById(POINT_B.id!!) }.returns(null)

        assertThrows<InvalidUseCaseException> { useCases.insert(CreatePathCommand(POINT_A.id!!, POINT_B.id!!)) }
    }

    @Test
    fun delete(): Unit = runBlocking {
        val eventCaptor = slot<PathDeletedEvent>()
        every { publisher.publishEvent(any()) }.returns(Unit)
        coEvery { repository.delete(1, 2) }.returns(Unit)

        useCases.delete(1, 2)

        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.pointAId).isEqualTo(1)
        assertThat(eventCaptor.captured.pointBId).isEqualTo(2)
    }

    @Test
    fun deleteReversed(): Unit = runBlocking {
        val eventCaptor = slot<PathDeletedEvent>()
        every { publisher.publishEvent(any()) }.returns(Unit)
        coEvery { repository.delete(1, 2) }.returns(Unit)

        useCases.delete(2, 1)

        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.pointAId).isEqualTo(1)
        assertThat(eventCaptor.captured.pointBId).isEqualTo(2)
    }

    @Test
    fun handleEventPointUpdated(): Unit = runBlocking {
        val pathCaptor = slot<Path>()
        coEvery { repository.update(any()) }.returns(PATH)
        every { publisher.publishEvent(any()) }.returns(Unit)
        every { repository.findAllContainingPointId(1) }.returns(flowOf(PATH))

        useCases.handleEvent(PointUpdatedEvent(this, 1))

        coVerify { repository.update(capture(pathCaptor)) }
        assertThat(pathCaptor.captured.meters).isNotEqualTo(PATH.meters)
        assertThat(pathCaptor.captured.meters).isEqualTo(1.57)
    }

    @Test
    fun handleEventPointDeleted(): Unit = runBlocking {
        val eventCaptor = slot<PathDeletedEvent>()
        coEvery { repository.delete(any(), any()) }.returns(Unit)
        every { publisher.publishEvent(any()) }.returns(Unit)
        every { repository.findAllContainingPointId(1) }.returns(flowOf(PATH))

        useCases.handleEvent(PointDeletedEvent(this, 1))

        coVerify { repository.delete(1, 2) }
        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.pointAId).isEqualTo(1)
        assertThat(eventCaptor.captured.pointBId).isEqualTo(2)
    }
}