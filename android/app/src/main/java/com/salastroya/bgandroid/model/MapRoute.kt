package com.salastroya.bgandroid.model

data class MapRoute(
    val id: Int = 0,
    val name: String = "",
    var points: MutableList<MapPoint> = mutableListOf()
) : java.io.Serializable {

    fun containsAnyBeacon(beacons: List<BeaconDto>): Boolean {
        val items = points
            .flatMap { it.items }
            .distinct()

        return beacons
            .mapNotNull { it.item }
            .any { items.contains(it) }
    }
}