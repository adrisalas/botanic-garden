package com.salastroya.bgserver.infrastructure.gamification.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("gamification_find_plant_user")
data class PlantFoundByUserDto(
    @Id val gameId: Int? = null, //PK Due to limitations of R2DBC we cannot set composite PKs
    val username: String, //PK Due to limitations of R2DBC we cannot set composite PKs
)