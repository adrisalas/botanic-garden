package com.salastroya.bgserver.infrastructure.poi.mapper

import com.salastroya.bgserver.core.poi.model.Poi
import com.salastroya.bgserver.infrastructure.poi.dto.PoiDto

fun PoiDto.toModel(): Poi {
    return Poi(
        id = this.id,
        description = this.description,
        image = this.image
    )
}

fun Poi.toDto(): PoiDto {
    return PoiDto(
        id = this.id,
        description = this.description,
        image = this.image
    )
}