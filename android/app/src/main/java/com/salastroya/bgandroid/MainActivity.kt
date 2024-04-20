package com.salastroya.bgandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.salastroya.bgandroid.pages.HomePage
import com.salastroya.bgandroid.pages.NearbyPlantsPage
import com.salastroya.bgandroid.pages.Page404
import com.salastroya.bgandroid.pages.PlantDetailPage
import com.salastroya.bgandroid.pages.SearchPage
import com.salastroya.bgandroid.pages.contentLogin
import com.salastroya.bgandroid.pages.contentSignUp
import com.salastroya.bgandroid.services.BottomNavigation
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.ui.theme.BotanicGardenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AllContent(navController)
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AllContent(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    BotanicGardenTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(navController)
                },
                floatingActionButton = {
                    if (currentRoute != "nearbyPlants") {
                        FloatingActionButton(onClick = { navController.navigate(Routes.nearbyPlants) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_beacons),
                                contentDescription = "Search near plants"
                            )
                        }
                    }
                }
            ) {
                MainScreenNavigationConfigurations(navController)
            }
        }
    }
}

@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Routes.home) {
        composable(route = Routes.home) {
            HomePage(navController)
        }
        composable(route = Routes.search) {
            SearchPage(navController)
        }
        composable(route = Routes.plantDetail + "/{plantId}") {
            val plantId = it.arguments?.getString("plantId")?.toIntOrNull() ?: 0
            PlantDetailPage(plantId, navController)
        }
        composable(route = Routes.nearbyPlants) {
            NearbyPlantsPage(navController)
        }
        composable(route = Routes.news) {
            Page404()
        }
        composable(route = Routes.map) {
            Page404()
        }
        composable(route = Routes.profile) {
            Page404()
        }
        composable(route = Routes.login) {
            contentLogin(navController = navController)
        }
        composable(route = Routes.signUp ){
            contentSignUp(navController = navController)
        }
    }
}



