package com.salastroya.bgserver.infrastructure.beacon

import com.salastroya.bgserver.core.beacon.ItemType
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("beacon")
data class BeaconDto(
    @Id val id: String,
    val itemType: ItemType? = null,
    val itemId: Int? = null
)