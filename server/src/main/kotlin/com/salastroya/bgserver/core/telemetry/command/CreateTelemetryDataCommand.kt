package com.salastroya.bgserver.core.telemetry.command

import com.salastroya.bgserver.core.beacon.model.Beacon
import java.time.LocalDateTime

data class CreateTelemetryDataCommand(
    val beacon: Beacon,
    val username: String,
    val dateTime: LocalDateTime
)
