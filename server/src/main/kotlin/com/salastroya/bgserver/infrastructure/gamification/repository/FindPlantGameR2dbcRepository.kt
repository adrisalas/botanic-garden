package com.salastroya.bgserver.infrastructure.gamification.repository

import com.salastroya.bgserver.infrastructure.gamification.dto.FindPlantGameDto
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FindPlantGameR2dbcRepository : CoroutineCrudRepository<FindPlantGameDto, Int> {
    suspend fun findFirstByOrderByIdDesc(): FindPlantGameDto?
    suspend fun insert(plantToBeFoundDto: FindPlantGameDto): FindPlantGameDto
    suspend fun update(plantToBeFoundDto: FindPlantGameDto): FindPlantGameDto
}