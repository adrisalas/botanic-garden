package com.salastroya.bgserver.infrastructure.map.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("map_point")
data class PointDto(
    @Id val id: Int?,
    val lat: Double,
    val lon: Double
)