package com.salastroya.bgserver.core.gamification

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.gamification.model.FindPlantGame
import com.salastroya.bgserver.core.gamification.repository.FindPlantGameRepository
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.model.PlantDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class FindPlantGameUseCasesTest {

    companion object {
        val USERNAME = "user"
        val ID = 23
        val PLANT_ID = 11
        val START = LocalDateTime.now()
        val GAME = FindPlantGame(ID, PLANT_ID, START)
        val PLANT = Plant(
            id = PLANT_ID,
            commonName = "",
            scientificName = "",
            description = "",
            image = "",
            type = "",
            details = PlantDetails(null, null, null, null)
        )
    }

    @MockK
    lateinit var plantUseCases: PlantUseCases

    @MockK
    lateinit var repository: FindPlantGameRepository

    @InjectMockKs
    lateinit var useCases: FindPlantGameUseCases

    @Test
    fun getPlantToBeFound(): Unit = runBlocking {
        coEvery { repository.findLastGame() }.returns(GAME)
        coEvery { plantUseCases.findById(PLANT_ID) }.returns(PLANT)

        val plant = useCases.getPlantToBeFound()

        assertThat(plant).isEqualTo(PLANT)
    }

    @Test
    fun getPlantToBeFoundNoGame(): Unit = runBlocking {
        coEvery { repository.findLastGame() }.returns(null)

        val plant = useCases.getPlantToBeFound()

        assertThat(plant).isNull()
    }

    @Test
    fun getPlantToBeFoundGameAlreadyFinished(): Unit = runBlocking {
        coEvery { repository.findLastGame() }.returns(GAME.copy(end = START))

        val plant = useCases.getPlantToBeFound()

        assertThat(plant).isNull()
    }

    @Test
    fun getPlantToBeFoundGameOfInvalidPlant(): Unit = runBlocking {
        coEvery { repository.findLastGame() }.returns(GAME)
        coEvery { plantUseCases.findById(PLANT_ID) }.returns(null)

        val plant = useCases.getPlantToBeFound()

        assertThat(plant).isNull()
    }

    @Test
    fun setPlant(): Unit = runBlocking {
        val newPlantToFind = 2
        val insertCaptor = slot<FindPlantGame>()
        val updateCaptor = slot<FindPlantGame>()
        coEvery { repository.findLastGame() }.returns(GAME)
        coEvery { repository.insert(any()) }.returns(GAME)
        coEvery { repository.update(any()) }.returns(GAME)
        coEvery { plantUseCases.existsById(newPlantToFind) }.returns(true)

        useCases.setPlant(newPlantToFind)

        coVerify { repository.update(capture(updateCaptor)) }
        assertThat(updateCaptor.captured.id).isEqualTo(GAME.id)
        assertThat(updateCaptor.captured.plantId).isEqualTo(GAME.plantId)
        assertThat(updateCaptor.captured.start).isEqualTo(GAME.start)
        assertThat(updateCaptor.captured.end).isNotNull()

        coVerify { repository.insert(capture(insertCaptor)) }
        assertThat(insertCaptor.captured.id).isNull()
        assertThat(insertCaptor.captured.plantId).isEqualTo(newPlantToFind)
        assertThat(insertCaptor.captured.start).isNotNull()
        assertThat(insertCaptor.captured.end).isNull()
    }

    @Test
    fun setPlantNoExistingPlant(): Unit = runBlocking {
        val newPlantToFind = 2
        coEvery { repository.findLastGame() }.returns(GAME)
        coEvery { plantUseCases.existsById(newPlantToFind) }.returns(false)

        assertThrows<InvalidUseCaseException> { useCases.setPlant(newPlantToFind) }

        coVerify(exactly = 0) { repository.update(any()) }
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun deactivateLastGame(): Unit = runBlocking {
        val updateCaptor = slot<FindPlantGame>()
        coEvery { repository.findLastGame() }.returns(GAME)
        coEvery { repository.update(any()) }.returns(GAME)
        assertThat(GAME.end).isNull()

        useCases.deactivateLastGame()

        coVerify { repository.update(capture(updateCaptor)) }
        assertThat(updateCaptor.captured.end).isNotNull()
    }

    @Test
    fun getUserPoints(): Unit = runBlocking {
        coEvery { repository.getNumberGamesCompletedBy(USERNAME) }.returns(4)

        val points = useCases.getUserPoints(USERNAME)

        assertThat(points).isEqualTo(100)
    }

    @Test
    fun getUserPoints2(): Unit = runBlocking {
        coEvery { repository.getNumberGamesCompletedBy(USERNAME) }.returns(1)

        val points = useCases.getUserPoints(USERNAME)

        assertThat(points).isEqualTo(25)
    }

    @Test
    fun gameCompletedBy(): Unit = runBlocking {
        coEvery { repository.setGameCompletedBy(ID, USERNAME) }.returns(Unit)
        coEvery { repository.findLastGame() }.returns(GAME)

        useCases.plantFoundBy(PLANT_ID, USERNAME)

        coVerify { repository.setGameCompletedBy(eq(ID), eq(USERNAME)) }
    }
}