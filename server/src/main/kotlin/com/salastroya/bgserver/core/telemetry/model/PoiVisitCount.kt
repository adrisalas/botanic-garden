package com.salastroya.bgserver.core.telemetry.model

import com.salastroya.bgserver.core.poi.model.Poi

data class PoiVisitCount(val poi: Poi, val count: Long)