package com.salastroya.bgandroid.model

import com.salastroya.bgandroid.R
import java.time.LocalDate
import java.time.Month

data class PlantDetail(
    val season: Season?,
    val leafType: String?,
    val water: String?,
    val flowering: Pair<Month, Month>?
) {

    fun getIconDetail(type: String): GardenIcon {
        return when (type) {
            "leafType" -> GardenIcon(
                iconId = R.drawable.leaf,
                colorId = R.color.green_leaf
            )

            "water" -> GardenIcon(
                iconId = R.drawable.water,
                colorId = R.color.teal_200
            )

            "flowering" -> GardenIcon(
                iconId = R.drawable.flowering,
                colorId = R.color.purple_custom
            )

            else -> {
                GardenIcon(
                    iconId = R.drawable.error,
                    colorId = R.color.black
                )
            }
        }
    }

    fun isBlooming(): Boolean {
        val flowering = this.flowering ?: return false

        val floweringBegin = flowering.first.value
        val floweringEnd = flowering.second.value
        val currentMonth = LocalDate.now().month.value

        return if (floweringBegin < floweringEnd) {
            currentMonth in floweringBegin..floweringEnd
        } else {
            currentMonth !in floweringEnd..floweringBegin
        }

    }
}
