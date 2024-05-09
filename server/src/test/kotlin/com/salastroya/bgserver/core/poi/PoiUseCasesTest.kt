package com.salastroya.bgserver.core.poi

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.poi.event.PoiDeletedEvent
import com.salastroya.bgserver.core.poi.model.Poi
import com.salastroya.bgserver.core.poi.port.PoiRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class PoiUseCasesTest {
    companion object {
        val ID = 1
        val NAME = "name"
        val DESCRIPTION = "description"
        val IMAGE = "image"
        val POI = Poi(
            id = ID,
            name = NAME,
            description = DESCRIPTION,
            image = IMAGE
        )
    }

    @MockK
    lateinit var publisher: ApplicationEventPublisher

    @MockK
    lateinit var repository: PoiRepository

    @InjectMockKs
    lateinit var useCases: PoiUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(POI))

        val pois = useCases.findAll().toList()

        assertThat(pois).hasSize(1)
        assertThat(pois[0].id).isEqualTo(ID)
        assertThat(pois[0].name).isEqualTo(NAME)
        assertThat(pois[0].description).isEqualTo(DESCRIPTION)
        assertThat(pois[0].image).isEqualTo(IMAGE)

    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(POI)

        val poi = useCases.findById(ID)

        assertThat(poi).isNotNull()
        assertThat(poi?.id).isEqualTo(ID)
        assertThat(poi?.name).isEqualTo(NAME)
        assertThat(poi?.description).isEqualTo(DESCRIPTION)
        assertThat(poi?.image).isEqualTo(IMAGE)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)

        val exists = useCases.existsById(ID)

        assertThat(exists).isEqualTo(true)
    }

    @Test
    fun insertWithId(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.insert(POI) }
    }

    @Test
    fun insertWithoutId(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(POI)

        val poi = useCases.insert(POI.copy(id = null))

        assertThat(poi).isEqualTo(POI)
    }

    @Test
    fun update(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { repository.update(any()) }.returns(POI)

        val poi = useCases.update(POI)

        assertThat(poi).isEqualTo(POI)
    }

    @Test
    fun updateWithoutID(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.update(POI.copy(id = null)) }
    }

    @Test
    fun updateNonExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(POI) }
    }

    @Test
    fun delete(): Unit = runBlocking {
        every { publisher.publishEvent(any()) }.returns(Unit)
        coEvery { useCases.delete(any()) }.returns(Unit)
        val eventCaptor = slot<PoiDeletedEvent>()

        useCases.delete(ID)

        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.id).isEqualTo(ID)
    }
}