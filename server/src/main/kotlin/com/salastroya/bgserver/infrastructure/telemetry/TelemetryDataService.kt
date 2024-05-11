package com.salastroya.bgserver.infrastructure.telemetry

import com.salastroya.bgserver.core.telemetry.command.CreateTelemetryDataCommand
import com.salastroya.bgserver.core.telemetry.model.ItemIdVisitCount
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerDay
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerHour
import com.salastroya.bgserver.core.telemetry.repository.TelemetryRepository
import com.salastroya.bgserver.infrastructure.telemetry.mapper.toDto
import com.salastroya.bgserver.infrastructure.telemetry.repository.TelemetryR2dbcRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service

@Service
class TelemetryDataService(
    private val repository: TelemetryR2dbcRepository
) : TelemetryRepository {
    override suspend fun insert(telemetryData: CreateTelemetryDataCommand) {
        repository.insert(telemetryData.toDto())
    }

    override fun mostVisitedPois(): Flow<ItemIdVisitCount> {
        return repository.poiVisitCount()
            .map { ItemIdVisitCount(it.itemId, it.visits) }
    }

    override fun mostVisitedPlants(): Flow<ItemIdVisitCount> {
        return repository.plantsVisitCount()
            .map { ItemIdVisitCount(it.itemId, it.visits) }
    }

    override fun visitorsPerHour(): Flow<VisitorsPerHour> {
        return repository.visitorsPerHour()
            .map { VisitorsPerHour(it.hourOfDay, it.uniqueVisitors) }
    }

    override fun visitorsPerDay(): Flow<VisitorsPerDay> {
        return repository.visitorsPerDay()
            .map { VisitorsPerDay(it.day, it.uniqueVisitors) }
    }
}