package com.salastroya.bgserver.infrastructure.map.mapper

import com.salastroya.bgserver.core.map.model.Item
import com.salastroya.bgserver.core.map.model.Point
import com.salastroya.bgserver.infrastructure.map.dto.PointDto
import com.salastroya.bgserver.infrastructure.map.dto.PointItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun Flow<PointItemDto>.toModel(): Flow<Item> {
    return this.map {
        Item(
            type = it.itemType,
            id = it.itemId
        )
    }
}

fun Point.toPointDto(): PointDto {
    return PointDto(
        id = this.id,
        lat = this.lat,
        lon = this.lon
    )
}

fun toDto(items: List<Item>, pointId: Int): List<PointItemDto> {
    return items
        .map {
            PointItemDto(
                pointId = pointId,
                itemType = it.type,
                itemId = it.id
            )
        }
}