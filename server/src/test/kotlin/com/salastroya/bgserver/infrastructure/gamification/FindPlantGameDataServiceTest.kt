package com.salastroya.bgserver.infrastructure.gamification

import com.salastroya.bgserver.configuration.R2bcConfiguration
import com.salastroya.bgserver.core.gamification.model.FindPlantGame
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime

@DataR2dbcTest
@Import(FindPlantGameDataService::class, R2bcConfiguration::class)
class FindPlantGameDataServiceTest {

    @Autowired
    lateinit var findPlantGameDataService: FindPlantGameDataService

    @Test
    fun playGameTest(): Unit = runBlocking {
        val username = "user"
        val noGame = findPlantGameDataService.findLastGame()
        assertThat(noGame).isNull()

        findPlantGameDataService.insert(FindPlantGame(plantId = 1))

        val firstGame = findPlantGameDataService.findLastGame()
        assertThat(firstGame).isNotNull()
        assertThat(firstGame?.plantId).isEqualTo(1)
        assertThat(firstGame?.end).isNull()

        findPlantGameDataService.setGameCompletedBy(firstGame!!.id!!, username)

        findPlantGameDataService.insert(FindPlantGame(plantId = 2))

        val secondGame = findPlantGameDataService.findLastGame()
        assertThat(secondGame).isNotNull()
        assertThat(secondGame?.plantId).isEqualTo(2)
        assertThat(secondGame?.end).isNull()

        findPlantGameDataService.setGameCompletedBy(secondGame!!.id!!, username)

        findPlantGameDataService.update(secondGame.copy(end = LocalDateTime.now()))

        val plantsFound = findPlantGameDataService.getNumberGamesCompletedBy(username)
        assertThat(plantsFound).isEqualTo(2)
    }
}