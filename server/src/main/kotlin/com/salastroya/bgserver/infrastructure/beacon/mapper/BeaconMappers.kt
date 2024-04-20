package com.salastroya.bgserver.infrastructure.beacon.mapper

import com.salastroya.bgserver.core.beacon.model.Beacon
import com.salastroya.bgserver.core.beacon.model.Item
import com.salastroya.bgserver.core.beacon.valueobject.BeaconId
import com.salastroya.bgserver.infrastructure.beacon.dto.BeaconDto

fun BeaconDto.toModel(): Beacon {
    if (this.itemId == null || this.itemType == null) {
        return Beacon(BeaconId(this.id))
    }

    return Beacon(BeaconId(this.id), Item(this.itemType, this.itemId))
}

fun Beacon.toDto(): BeaconDto {
    if (this.item == null) {
        return BeaconDto(this.id)
    }

    return BeaconDto(this.id, this.item.type, this.item.id)
}