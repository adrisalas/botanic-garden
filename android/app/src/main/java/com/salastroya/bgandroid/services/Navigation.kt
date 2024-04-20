package com.salastroya.bgandroid.services

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.salastroya.bgandroid.model.BottomNavItem

@Composable
fun BottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.News,
        BottomNavItem.Map,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            AddItem(
                navItem = item,
                navController = navController,
                isSelected = selected
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    navItem: BottomNavItem,
    navController: NavHostController,
    isSelected: Boolean
) {
    NavigationBarItem(
        label = {
            Text(text = navItem.title)
        },
        icon = {
            Icon(
                painterResource(id = navItem.icon),
                contentDescription = navItem.title
            )
        },
        selected = isSelected,
        alwaysShowLabel = true,
        onClick = {
            navController.navigate(navItem.route)
        },
        colors = NavigationBarItemDefaults.colors()
    )
}