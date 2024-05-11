package com.salastroya.bgserver.infrastructure.telemetry.dto

import com.salastroya.bgserver.core.beacon.model.ItemType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("telemetry_data")
data class TelemetryDataDto(
    @Id val id: Long?,
    val beaconId: String,
    val itemType: ItemType? = null,
    val itemId: Int? = null,
    val username: String,
    val dateTime: LocalDateTime
)
