package com.salastroya.bgserver.infrastructure.beacon

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("beacon")
data class BeaconDto(@Id val id: String)