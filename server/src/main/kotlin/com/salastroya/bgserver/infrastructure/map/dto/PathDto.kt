package com.salastroya.bgserver.infrastructure.map.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("map_path")
data class PathDto(
    @Id val pointAId: Int, // PK Due to limitations of R2DBC we cannot set composite PKs
    val pointBId: Int, // PK Due to limitations of R2DBC we cannot set composite PKs
    val meters: Double
)