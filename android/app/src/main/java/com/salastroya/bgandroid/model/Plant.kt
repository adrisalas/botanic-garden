package com.salastroya.bgandroid.model

data class Plant(
    val id: Int,
    val commonName: String,
    val scientificName: String,
    val description: String? = null,
    val image: String? = null,
    val type: String? = null,
    val details: PlantDetail? = null
) {

    fun doesMatchSearchQuery(query: String): Boolean {
        return listOf(
            commonName.lowercase(),
            scientificName.lowercase()
        ).any { it.contains(query.trim().lowercase()) }
    }

    fun doesMatchBloomingFilter(bloomingFilter: Boolean): Boolean {
        if (!bloomingFilter) {
            return true
        }
        return details?.isBlooming() ?: false
    }
}