package com.salastroya.bgandroid.model

data class MapPoint(
    val id: Int,
    val lat: Float,
    val lon: Float,
    val items: List<Item>
) : java.io.Serializable