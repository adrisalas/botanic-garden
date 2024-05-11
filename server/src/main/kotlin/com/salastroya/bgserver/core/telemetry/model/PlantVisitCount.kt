package com.salastroya.bgserver.core.telemetry.model

import com.salastroya.bgserver.core.plant.model.Plant

data class PlantVisitCount(val plant: Plant, val count: Long)