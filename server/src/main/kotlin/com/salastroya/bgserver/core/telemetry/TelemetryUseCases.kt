package com.salastroya.bgserver.core.telemetry

import com.salastroya.bgserver.core.beacon.BeaconUseCases
import com.salastroya.bgserver.core.beacon.model.ItemType
import com.salastroya.bgserver.core.common.exception.InvalidUseCaseException
import com.salastroya.bgserver.core.gamification.FindPlantGameUseCases
import com.salastroya.bgserver.core.plant.PlantUseCases
import com.salastroya.bgserver.core.poi.PoiUseCases
import com.salastroya.bgserver.core.telemetry.command.CreateTelemetryDataCommand
import com.salastroya.bgserver.core.telemetry.model.PlantVisitCount
import com.salastroya.bgserver.core.telemetry.model.PoiVisitCount
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerDay
import com.salastroya.bgserver.core.telemetry.model.VisitorsPerHour
import com.salastroya.bgserver.core.telemetry.repository.TelemetryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TelemetryUseCases(
    private val repository: TelemetryRepository,
    private val findPlantGameUseCases: FindPlantGameUseCases,
    private val poiUseCases: PoiUseCases,
    private val plantUseCases: PlantUseCases,
    private val beaconUseCases: BeaconUseCases
) {

    suspend fun beaconSeenBy(beaconId: String, username: String) {
        val beacon = beaconUseCases.findById(beaconId)
            ?: throw InvalidUseCaseException("Beacon $beaconId does not exist")

        if (beacon.item?.type == ItemType.PLANT) {
            val plantId = beacon.item.id
            findPlantGameUseCases.plantFoundBy(plantId, username)
        }

        repository.insert(CreateTelemetryDataCommand(beacon, username, LocalDateTime.now()))
    }

    fun mostVisitedPois(): Flow<PoiVisitCount> {
        return repository.mostVisitedPois()
            .map {
                val poi = poiUseCases.findById(it.itemId) ?: return@map null

                return@map PoiVisitCount(poi, it.count)
            }.filterNotNull()
    }

    fun mostVisitedPlants(): Flow<PlantVisitCount> {
        return repository.mostVisitedPlants()
            .map {
                val plant = plantUseCases.findById(it.itemId) ?: return@map null

                return@map PlantVisitCount(plant, it.count)
            }.filterNotNull()
    }

    fun visitorsPerHour(): Flow<VisitorsPerHour> {
        return repository.visitorsPerHour()
    }

    fun visitorsPerDay(): Flow<VisitorsPerDay> {
        return repository.visitorsPerDay()
    }
}