package com.salastroya.bgserver.core.beacon.model

import com.salastroya.bgserver.core.beacon.valueobject.BeaconId

data class Beacon(private val beaconId: BeaconId, val item: Item? = null) {
    val id: String
        get() = this.beaconId.value
}