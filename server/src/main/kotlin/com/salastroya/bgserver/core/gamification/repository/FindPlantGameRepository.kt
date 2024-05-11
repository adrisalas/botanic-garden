package com.salastroya.bgserver.core.gamification.repository

import com.salastroya.bgserver.core.gamification.model.FindPlantGame

interface FindPlantGameRepository {
    suspend fun findLastGame(): FindPlantGame?
    suspend fun insert(findPlantGame: FindPlantGame): FindPlantGame
    suspend fun update(findPlantGame: FindPlantGame): FindPlantGame
    suspend fun getNumberGamesCompletedBy(username: String): Int
    suspend fun setGameCompletedBy(gameId: Int, username: String)
}
