package com.salastroya.bgserver.infrastructure.map.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("map_route")
data class RouteDto(
    @Id val id: Int?,
    val name: String,
    val associatedUser: String?
)
