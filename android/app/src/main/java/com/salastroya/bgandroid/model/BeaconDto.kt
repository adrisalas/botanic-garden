package com.salastroya.bgandroid.model

enum class ItemType {
    PLANT,
    LOCATION
}

data class Item(val type: ItemType, val id: Int)

data class BeaconDto(val id: String, val item: Item? = null)
