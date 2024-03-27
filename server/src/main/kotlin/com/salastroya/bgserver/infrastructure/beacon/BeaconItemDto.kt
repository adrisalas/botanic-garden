package com.salastroya.bgserver.infrastructure.beacon

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("beacon_item")
data class BeaconItemDto(@Id val id: String, val itemId: Long)