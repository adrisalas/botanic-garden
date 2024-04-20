package com.salastroya.bgandroid.model

import com.salastroya.bgandroid.R.color.blue_winter
import com.salastroya.bgandroid.R.color.orange_autumn
import com.salastroya.bgandroid.R.color.pink_spring
import com.salastroya.bgandroid.R.color.yellow_summer
import com.salastroya.bgandroid.R.drawable.autumn_icon
import com.salastroya.bgandroid.R.drawable.spring_icon
import com.salastroya.bgandroid.R.drawable.summer_icon
import com.salastroya.bgandroid.R.drawable.winter_icon

enum class Season(val seasonName: String) {
    WINTER("Winter"),
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall");

    fun getIcon(): GardenIcon {
        return when (this) {
            SPRING -> GardenIcon(iconId = spring_icon, colorId = pink_spring)
            SUMMER -> GardenIcon(iconId = summer_icon, colorId = yellow_summer)
            FALL -> GardenIcon(iconId = autumn_icon, colorId = orange_autumn)
            WINTER -> GardenIcon(iconId = winter_icon, colorId = blue_winter)

        }
    }
}
