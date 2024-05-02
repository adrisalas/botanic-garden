package com.salastroya.bgserver.infrastructure.map.dto

import com.salastroya.bgserver.core.map.model.ItemType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("map_point_item")
data class PointItemDto(
    @Id val pointId: Int, //PK Due to limitations of R2DBC we cannot set composite PKs
    val itemType: ItemType, // PK Due to limitations of R2DBC we cannot set composite PKs
    val itemId: Int // PK Due to limitations of R2DBC we cannot set composite PKs
)