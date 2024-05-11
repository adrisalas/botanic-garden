package com.salastroya.bgserver.core.gamification

import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.gamification.model.FindPlantGame
import com.salastroya.bgserver.core.gamification.repository.FindPlantGameRepository
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.plant.model.Plant
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val POINTS_PER_PLANT = 25

@Service
class FindPlantGameUseCases(
    private val plantUseCases: PlantUseCases,
    private val repository: FindPlantGameRepository
) {
    suspend fun getPlantToBeFound(): Plant? {
        val game = repository.findLastGame()

        if (game == null || game.end != null) {
            return null
        }

        return plantUseCases.findById(game.plantId)
    }

    @Throws(InvalidUseCaseException::class)
    suspend fun setPlant(plantId: Int) {
        if (!plantUseCases.existsById(plantId)) {
            throw InvalidUseCaseException("Plant with id $plantId does not exists")
        }

        deactivateLastGame()

        repository.insert(FindPlantGame(plantId = plantId))
    }

    suspend fun deactivateLastGame() {
        repository.findLastGame()?.let { game ->
            if (game.isActive()) {
                repository.update(game.copy(end = LocalDateTime.now()))
            }
        }
    }

    suspend fun getUserPoints(username: String): Int {
        return POINTS_PER_PLANT * repository.getNumberGamesCompletedBy(username)
    }

    suspend fun plantFoundBy(plantId: Int, username: String) {
        repository.findLastGame()?.let { game ->
            if (game.isActive() && game.plantId == plantId) {
                repository.setGameCompletedBy(game.id!!, username)
            }
        }
    }
}