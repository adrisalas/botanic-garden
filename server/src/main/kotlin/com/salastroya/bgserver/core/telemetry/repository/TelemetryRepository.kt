package com.salastroya.bgserver.core.telemetry.repository

import com.salastroya.bgserver.core.telemetry.command.CreateTelemetryDataCommand
import com.salastroya.bgserver.core.telemetry.model.ItemIdVisitCount
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerDay
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerHour
import kotlinx.coroutines.flow.Flow

interface TelemetryRepository {
    suspend fun insert(telemetryData: CreateTelemetryDataCommand)
    fun mostVisitedPois(): Flow<ItemIdVisitCount>
    fun mostVisitedPlants(): Flow<ItemIdVisitCount>
    fun visitorsPerHour(): Flow<VisitorsPerHour>
    fun visitorsPerDay(): Flow<VisitorsPerDay>
}