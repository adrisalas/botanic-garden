package com.salastroya.bgandroid.model

import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.services.Routes

sealed class BottomNavItem(
    var title: String,
    var icon: Int,
    var route: String
) {
    data object Home :
        BottomNavItem(
            "Home",
            R.drawable.baseline_home_24,
            route = Routes.home
        )

    data object Search :
        BottomNavItem(
            "Search",
            R.drawable.icon_search,
            route = Routes.search
        )

    data object News :
        BottomNavItem(
            "News",
            R.drawable.icon_news,
            route = Routes.news
        )

    data object Map :
        BottomNavItem(
            "Map",
            R.drawable.icon_map,
            route = Routes.map
        )

    data object Profile :
        BottomNavItem(
            "Profile",
            R.drawable.icon_profile,
            route = Routes.profile
        )
}