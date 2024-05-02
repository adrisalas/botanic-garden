package com.salastroya.bgserver.core.map.model

data class Point(
    val id: Int?,
    val lat: Double,
    val lon: Double,
    val items: List<Item>
)