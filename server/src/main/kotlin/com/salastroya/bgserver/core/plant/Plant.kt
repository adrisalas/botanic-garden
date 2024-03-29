package com.salastroya.bgserver.core.plant

data class Plant(
    val id: Int?,
    val commonName: String,
    val scientificName: String,
    val description: String,
    val image: String,
    val type: String,
    val details: PlantDetails
)