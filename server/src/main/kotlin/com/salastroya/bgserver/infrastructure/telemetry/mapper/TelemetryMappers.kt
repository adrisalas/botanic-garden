package com.salastroya.bgserver.infrastructure.telemetry.mapper

import com.salastroya.bgserver.core.telemetry.command.CreateTelemetryDataCommand
import com.salastroya.bgserver.infrastructure.telemetry.dto.TelemetryDataDto

fun CreateTelemetryDataCommand.toDto(): TelemetryDataDto {
    return TelemetryDataDto(
        id = null,
        beaconId = this.beacon.id,
        itemType = this.beacon.item?.type,
        itemId = this.beacon.item?.id,
        username = this.username,
        dateTime = this.dateTime
    )
}