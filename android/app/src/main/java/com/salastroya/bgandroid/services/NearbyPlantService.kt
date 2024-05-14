package com.salastroya.bgandroid.services

import com.salastroya.bgandroid.model.ItemType
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.beacons.BeaconService
import com.salastroya.bgandroid.services.beacons.NearbyBeaconService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import org.altbeacon.beacon.Beacon

object NearbyPlantService {

    suspend fun getNearbyPlants(): List<Pair<Plant, Double>> {
        return NearbyBeaconService
            .getNearbyBeacons()
            .asFlow()
            .mapNotNull { it.toBeaconDtoWithDistance() }
            .filter { it.isAPlant() }
            .mapNotNull { it.toPlantIdWithDistance() }
            .mapNotNull { it.toPlantWithDistance() }
            .toList()

    }

    private suspend fun Beacon.toBeaconDtoWithDistance(): Pair<com.salastroya.bgandroid.model.BeaconDto, Double>? {
        return BeaconService.findByBeacon(this)
            ?.let { Pair(it, this.distance) }
    }

    private fun Pair<com.salastroya.bgandroid.model.BeaconDto?, Double>.isAPlant(): Boolean {
        return this.first?.item?.type == ItemType.PLANT
    }

    private fun Pair<com.salastroya.bgandroid.model.BeaconDto?, Double>.toPlantIdWithDistance(): Pair<Int, Double>? {
        return this.first?.item?.id?.let { Pair(it, this.second) }
    }

    private suspend fun Pair<Int, Double>.toPlantWithDistance(): Pair<Plant, Double>? {
        return PlantService.findById(this.first)
            ?.let { Pair(it, this.second) }
    }
}