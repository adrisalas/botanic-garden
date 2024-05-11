package com.salastroya.bgserver.infrastructure.gamification.mapper

import com.salastroya.bgserver.core.gamification.model.FindPlantGame
import com.salastroya.bgserver.infrastructure.gamification.dto.FindPlantGameDto

fun FindPlantGameDto.toModel(): FindPlantGame {
    return FindPlantGame(
        id = this.id,
        plantId = this.plantId,
        start = this.startDateTime,
        end = this.endDateTime
    )
}

fun FindPlantGame.toDto(): FindPlantGameDto {
    return FindPlantGameDto(
        id = this.id,
        plantId = this.plantId,
        startDateTime = this.start,
        endDateTime = this.end
    )
}