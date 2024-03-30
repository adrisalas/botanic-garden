package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.core.beacon.Beacon
import com.salastroya.bgserver.core.beacon.Item

fun BeaconDto.toModel(): Beacon {
    if (this.itemId == null || this.itemType == null) {
        return Beacon(this.id)
    }

    return Beacon(this.id, Item(this.itemType, this.itemId))
}

fun Beacon.toDto(): BeaconDto {
    if (this.item == null) {
        return BeaconDto(this.id)
    }

    return BeaconDto(this.id, this.item.type, this.item.id)
}