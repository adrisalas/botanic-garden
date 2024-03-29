package com.salastroya.bgserver.infrastructure.plant

import com.salastroya.bgserver.core.plant.Season
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Month

@Table("plant")
data class PlantDto(
    @Id val id: Int?,
    val commonName: String,
    val scientificName: String,
    val description: String,
    val image: String,
    val plantType: String,
    val season: Season?,
    val leafType: String?,
    val water: String?,
    val floweringBegin: Month?,
    val floweringEnd: Month?
)