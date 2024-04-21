package com.salastroya.bgserver.application.beacon

import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.valueobject.BeaconId

fun BeaconDto.toModel(): Beacon {
    return Beacon(BeaconId(this.id), this.item)
}

fun Beacon.toDto(): BeaconDto {
    return BeaconDto(this.id, this.item)
}