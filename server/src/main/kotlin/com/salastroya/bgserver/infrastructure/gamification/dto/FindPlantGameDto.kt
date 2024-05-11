package com.salastroya.bgserver.infrastructure.gamification.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("gamification_find_plant")
data class FindPlantGameDto(
    @Id val id: Int? = null,
    val plantId: Int,
    val startDateTime: LocalDateTime = LocalDateTime.now(),
    val endDateTime: LocalDateTime? = null
)