package com.salastroya.bgserver.infrastructure.gamification.repository

import com.salastroya.bgserver.infrastructure.gamification.dto.PlantFoundByUserDto
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PlantFoundByUserR2dbcRepository : CoroutineCrudRepository<PlantFoundByUserDto, Int> {
    suspend fun insert(plantFoundByUserDto: PlantFoundByUserDto): PlantFoundByUserDto
    suspend fun existsByGameIdAndUsername(gameId: Int, username: String): Boolean
    suspend fun countByUsername(username: String): Int
}