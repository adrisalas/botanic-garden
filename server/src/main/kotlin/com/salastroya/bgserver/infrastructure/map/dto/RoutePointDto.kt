package com.salastroya.bgserver.infrastructure.map.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("map_route_point")
data class RoutePointDto(
    @Id val routeId: Int, //PK Due to limitations of R2DBC we cannot set composite PKs
    val pointId: Int, //PK Due to limitations of R2DBC we cannot set composite PKs
    val position: Int //PK Due to limitations of R2DBC we cannot set composite PKs
)