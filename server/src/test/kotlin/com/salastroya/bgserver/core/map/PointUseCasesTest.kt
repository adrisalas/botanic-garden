package com.salastroya.bgserver.core.map

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.map.event.PointDeletedEvent
import com.salastroya.bgserver.core.map.event.PointUpdatedEvent
import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.ItemType.PLANT
import com.salastroya.bgserver.core.map.model.ItemType.POI
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.core.map.repository.PointRepository
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.event.PlantDeletedEvent
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.poi.event.PoiDeletedEvent
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
class PointUseCasesTest {
    companion object {
        val ID = 101
        val LAT = 10.0
        val LON = 10.0
        val TYPE = POI
        val ITEM = Item(TYPE, ID)
        val POINT = Point(ID, LAT, LON, listOf(ITEM))
    }


    @MockK
    lateinit var repository: PointRepository

    @MockK
    lateinit var plantUseCases: PlantUseCases

    @MockK
    lateinit var poiUseCases: PoiUseCases

    @MockK
    lateinit var publisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var useCases: PointUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(POINT))

        val points = useCases.findAll().toList()

        assertThat(points).hasSize(1)
        assertThat(points[0].id).isEqualTo(ID)
        assertThat(points[0].lat).isEqualTo(LAT)
        assertThat(points[0].lon).isEqualTo(LON)
        assertThat(points[0].items).hasSize(1)
        assertThat(points[0].items[0].id).isEqualTo(ID)
        assertThat(points[0].items[0].type).isEqualTo(TYPE)
    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(POINT)

        val point = useCases.findById(ID)

        assertThat(point).isNotNull()
        assertThat(point?.id).isEqualTo(ID)
        assertThat(point?.lat).isEqualTo(LAT)
        assertThat(point?.lon).isEqualTo(LON)
        assertThat(point?.items).hasSize(1)
        assertThat(point?.items?.get(0)?.id).isEqualTo(ID)
        assertThat(point?.items?.get(0)?.type).isEqualTo(TYPE)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)

        val exists = useCases.existsById(ID)

        assertThat(exists).isTrue()
    }

    @Test
    fun doesNotExistsById(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        val exists = useCases.existsById(ID)

        assertThat(exists).isFalse()
    }

    @Test
    fun insertWithId(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.insert(POINT) }
    }

    @Test
    fun insert(): Unit = runBlocking {
        coEvery { poiUseCases.existsById(ID) }.returns(true)
        coEvery { repository.insert(any()) }.returns(POINT)

        useCases.insert(POINT.copy(id = null))

        coVerify { repository.insert(any()) }
    }

    @Test
    fun insertWithPlant(): Unit = runBlocking {
        coEvery { plantUseCases.existsById(ID) }.returns(true)
        coEvery { repository.insert(any()) }.returns(POINT)

        useCases.insert(POINT.copy(id = null, items = listOf(ITEM.copy(PLANT))))

        coVerify { repository.insert(any()) }
    }

    @Test
    fun insertNoExistingItem(): Unit = runBlocking {
        coEvery { poiUseCases.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.insert(POINT.copy(id = null)) }
    }

    @Test
    fun updateWithIdNull(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.update(POINT.copy(id = null)) }
    }

    @Test
    fun updateNoExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(POINT) }
    }

    @Test
    fun updateNoExistingPoi(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { poiUseCases.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(POINT) }
    }

    @Test
    fun update(): Unit = runBlocking {
        val eventCaptor = slot<PointUpdatedEvent>()
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { repository.update(any()) }.returns(POINT)
        coEvery { poiUseCases.existsById(ID) }.returns(true)
        every { publisher.publishEvent(any()) }.returns(Unit)

        useCases.update(POINT)

        coVerify { repository.update(any()) }
        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.id).isEqualTo(ID)
    }

    @Test
    fun delete(): Unit = runBlocking {
        val eventCaptor = slot<PointDeletedEvent>()
        coEvery { repository.delete(ID) }.returns(Unit)
        every { publisher.publishEvent(any()) }.returns(Unit)

        useCases.delete(ID)

        coVerify { repository.delete(ID) }
        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.id).isEqualTo(ID)
    }

    @Test
    fun handleEventPlantDeleted(): Unit = runBlocking {
        val pointCaptor = slot<Point>()
        coEvery { repository.update(any()) }.returns(POINT)
        every { repository.findAll() }.returns(flowOf(POINT.copy(items = listOf(ITEM, ITEM.copy(PLANT)))))

        useCases.handleEvent(PlantDeletedEvent(this, ID))

        coVerify { repository.update(capture(pointCaptor)) }
        assertThat(pointCaptor.captured.items).hasSize(1)
        assertThat(pointCaptor.captured.items[0].type).isEqualTo(POI)
    }

    @Test
    fun handleEventPoiDeleted(): Unit = runBlocking {
        val pointCaptor = slot<Point>()
        coEvery { repository.update(any()) }.returns(POINT)
        every { repository.findAll() }.returns(flowOf(POINT.copy(items = listOf(ITEM, ITEM.copy(PLANT)))))

        useCases.handleEvent(PoiDeletedEvent(this, ID))

        coVerify { repository.update(capture(pointCaptor)) }
        assertThat(pointCaptor.captured.items).hasSize(1)
        assertThat(pointCaptor.captured.items[0].type).isEqualTo(PLANT)


    }
}