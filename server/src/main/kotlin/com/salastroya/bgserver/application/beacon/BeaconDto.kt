package com.salastroya.bgserver.application.beacon

import com.salastroya.bgserver.core.beacon.model.Item

data class BeaconDto(val id: String, val item: Item? = null)