package com.salastroya.bgserver.infrastructure.telemetry.dto

import java.time.LocalDate

data class VisitorsPerDayDto(
    val day: LocalDate,
    val uniqueVisitors: Long
)
