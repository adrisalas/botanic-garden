package com.salastroya.bgserver.infrastructure.gamification

import com.salastroya.bgserver.core.gamification.model.FindPlantGame
import com.salastroya.bgserver.core.gamification.repository.FindPlantGameRepository
import com.salastroya.bgserver.infrastructure.gamification.dto.PlantFoundByUserDto
import com.salastroya.bgserver.infrastructure.gamification.mapper.toDto
import com.salastroya.bgserver.infrastructure.gamification.mapper.toModel
import com.salastroya.bgserver.infrastructure.gamification.repository.FindPlantGameR2dbcRepository
import com.salastroya.bgserver.infrastructure.gamification.repository.PlantFoundByUserR2dbcRepository
import org.springframework.stereotype.Service

@Service
class FindPlantGameDataService(
    private val gameRepository: FindPlantGameR2dbcRepository,
    private val foundByUserRepository: PlantFoundByUserR2dbcRepository
) : FindPlantGameRepository {

    override suspend fun findLastGame(): FindPlantGame? {
        return gameRepository.findFirstByOrderByIdDesc()?.toModel()
    }

    override suspend fun insert(findPlantGame: FindPlantGame): FindPlantGame {
        return gameRepository.insert(findPlantGame.toDto()).toModel()
    }

    override suspend fun update(findPlantGame: FindPlantGame): FindPlantGame {
        return gameRepository.update(findPlantGame.toDto()).toModel()
    }

    override suspend fun setGameCompletedBy(gameId: Int, username: String) {
        if (!foundByUserRepository.existsByGameIdAndUsername(gameId, username)) {
            foundByUserRepository.insert(PlantFoundByUserDto(gameId, username))
        }
    }

    override suspend fun getNumberGamesCompletedBy(username: String): Int {
        return foundByUserRepository.countByUsername(username)
    }

}
