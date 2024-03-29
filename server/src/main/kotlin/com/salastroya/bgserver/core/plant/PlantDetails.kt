package com.salastroya.bgserver.core.plant

import java.time.Month

data class PlantDetails(
    val season: Season?,
    val leafType: String?,
    val water: String?,
    val flowering: Pair<Month, Month>?
)
