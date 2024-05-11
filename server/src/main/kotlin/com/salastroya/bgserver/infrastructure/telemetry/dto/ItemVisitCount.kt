package com.salastroya.bgserver.infrastructure.telemetry.dto

data class ItemVisitCount(
    val itemId: Int,
    val visits: Long,
)
