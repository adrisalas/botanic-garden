package com.salastroya.bgserver.core.plant

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.plant.event.PlantDeletedEvent
import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.model.PlantDetails
import com.salastroya.bgserver.core.plant.model.Season
import com.salastroya.bgserver.core.plant.repository.PlantRepository
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
import java.time.Month

@ExtendWith(MockKExtension::class)
class PlantUseCasesTest {

    companion object {
        val ID = 1
        val NAME = "name"
        val DESCRIPTION = "description"
        val IMAGE = "image"
        val TYPE = "type"
        val WATER = "Twice a week"
        val DETAILS = PlantDetails(
            season = Season.SUMMER,
            leafType = TYPE,
            water = WATER,
            flowering = Month.JUNE to Month.SEPTEMBER
        )
        val PLANT = Plant(
            id = ID,
            commonName = NAME,
            scientificName = NAME,
            description = DESCRIPTION,
            image = IMAGE,
            type = TYPE,
            details = DETAILS
        )
    }

    @MockK
    lateinit var publisher: ApplicationEventPublisher

    @MockK
    lateinit var repository: PlantRepository

    @InjectMockKs
    lateinit var useCases: PlantUseCases

    @Test
    fun findAll(): Unit = runBlocking {
        every { repository.findAll() }.returns(flowOf(PLANT))

        val plants = useCases.findAll().toList()

        assertThat(plants).hasSize(1)
        assertThat(plants[0].id).isEqualTo(ID)
        assertThat(plants[0].commonName).isEqualTo(NAME)
        assertThat(plants[0].scientificName).isEqualTo(NAME)
        assertThat(plants[0].description).isEqualTo(DESCRIPTION)
        assertThat(plants[0].image).isEqualTo(IMAGE)

    }

    @Test
    fun findById(): Unit = runBlocking {
        coEvery { repository.findById(ID) }.returns(PLANT)

        val plant = useCases.findById(ID)

        assertThat(plant).isNotNull()
        assertThat(plant?.id).isEqualTo(ID)
        assertThat(plant?.commonName).isEqualTo(NAME)
        assertThat(plant?.scientificName).isEqualTo(NAME)
        assertThat(plant?.description).isEqualTo(DESCRIPTION)
        assertThat(plant?.image).isEqualTo(IMAGE)
    }

    @Test
    fun existsById(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)

        val exists = useCases.existsById(ID)

        assertThat(exists).isEqualTo(true)
    }

    @Test
    fun insertWithId(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.insert(PLANT) }
    }

    @Test
    fun insertWithoutId(): Unit = runBlocking {
        coEvery { repository.insert(any()) }.returns(PLANT)

        val plant = useCases.insert(PLANT.copy(id = null))

        assertThat(plant).isEqualTo(PLANT)
    }

    @Test
    fun update(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(true)
        coEvery { repository.update(any()) }.returns(PLANT)

        val plant = useCases.update(PLANT)

        assertThat(plant).isEqualTo(PLANT)
    }

    @Test
    fun updateWithoutID(): Unit = runBlocking {
        assertThrows<InvalidUseCaseException> { useCases.update(PLANT.copy(id = null)) }
    }

    @Test
    fun updateNonExisting(): Unit = runBlocking {
        coEvery { repository.existsById(ID) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.update(PLANT) }
    }

    @Test
    fun delete(): Unit = runBlocking {
        every { publisher.publishEvent(any()) }.returns(Unit)
        coEvery { useCases.delete(ID) }.returns(Unit)
        val eventCaptor = slot<PlantDeletedEvent>()

        useCases.delete(ID)

        verify { publisher.publishEvent(capture(eventCaptor)) }
        assertThat(eventCaptor.captured.id).isEqualTo(ID)
    }
}