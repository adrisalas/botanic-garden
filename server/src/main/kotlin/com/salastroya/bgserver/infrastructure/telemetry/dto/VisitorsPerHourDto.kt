package com.salastroya.bgserver.infrastructure.telemetry.dto

data class VisitorsPerHourDto(
    val hourOfDay: Int,
    val uniqueVisitors: Long
)
