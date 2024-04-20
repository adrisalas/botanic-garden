package com.salastroya.bgserver.infrastructure.plant.mapper

import com.salastroya.bgserver.core.plant.model.Plant
import com.salastroya.bgserver.core.plant.model.PlantDetails
import com.salastroya.bgserver.infrastructure.plant.dto.PlantDto


fun Plant.toDto() = PlantDto(
    id = this.id,
    commonName = this.commonName,
    scientificName = this.scientificName,
    description = this.description,
    image = this.image,
    plantType = this.type,
    season = this.details.season,
    leafType = this.details.leafType,
    water = this.details.water,
    floweringBegin = this.details.flowering?.first,
    floweringEnd = this.details.flowering?.second
)

fun PlantDto.toModel(): Plant {
    val flowering =
        if (this.floweringBegin != null && this.floweringEnd != null)
            Pair(
                this.floweringBegin,
                this.floweringEnd
            )
        else
            null

    val details = PlantDetails(
        season = this.season,
        leafType = this.leafType,
        water = this.water,
        flowering = flowering
    )

    return Plant(
        id = this.id ?: -1,
        commonName = this.commonName,
        scientificName = this.scientificName,
        description = this.description,
        image = this.image,
        type = this.plantType,
        details = details
    )
}