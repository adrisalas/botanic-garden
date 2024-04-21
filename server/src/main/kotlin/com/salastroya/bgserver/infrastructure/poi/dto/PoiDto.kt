package com.salastroya.bgserver.infrastructure.poi.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("poi")
data class PoiDto(
    @Id val id: Int?,
    val description: String,
    val image: String,
)