package com.salastroya.bgserver.core.telemetry.model

import java.time.LocalDate

data class VisitorsPerDay(val day: LocalDate, val count: Long)