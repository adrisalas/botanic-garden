package com.salastroya.bgandroid.model

data class POI(
    val id: Int,
    val name: String,
    val description: String,
    val image: String? = null,
) {

}