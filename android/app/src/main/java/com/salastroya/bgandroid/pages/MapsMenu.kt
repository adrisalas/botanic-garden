package com.salastroya.bgandroid.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.model.BeaconDto
import com.salastroya.bgandroid.model.MapRoute
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.services.UserRouteService
import com.salastroya.bgandroid.services.auth.JWTService
import com.salastroya.bgandroid.services.beacons.BeaconService
import com.salastroya.bgandroid.services.beacons.NearbyBeaconService
import com.salastroya.bgandroid.ui.theme.fontNanum
import kotlinx.coroutines.delay

@Composable
fun MapsMenuPage(navController: NavController) {
    val tokenJWT = remember { JWTService.jwtStore }
    var myRoute by remember { mutableStateOf(MapRoute()) }
    var allRoutes by remember { mutableStateOf(emptyList<MapRoute>()) }

    LaunchedEffect(Unit) {
        myRoute = UserRouteService.findMyRoute()
        allRoutes = UserRouteService.findAllRoutes()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titleMapsMenu),
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp),
                color = Color.White
            )
        }


        if (tokenJWT.isNullOrEmpty()) {
            ContentIfNotLoggedMaps(navController = navController)
        } else {
            ContentIfLoggedMaps(navController = navController, myRoute, allRoutes)
        }
    }
}

@Composable
fun ContentIfLoggedMaps(
    navController: NavController,
    myRoute: MapRoute,
    allRoutes: List<MapRoute>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { navController.navigate(Routes.createRoute) },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.dark_green)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 16.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .height(55.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                //tint = colorResource(id = R.color.purple_500),
                contentDescription = null
            )
            Text(text = stringResource(id = R.string.buttonCreateRoute))
        }

        Divider(
            color = colorResource(id = R.color.teal_700),
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
        )

        UserRoute(myRoute, navController)

        Divider(
            color = colorResource(id = R.color.teal_700),
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
        )

        GenericRoutes(allRoutes, navController)
    }
}

@Composable
fun UserRoute(myRoute: MapRoute, navController: NavController) {
    Text(
        text = stringResource(id = R.string.titleMyRoute),
        fontWeight = FontWeight.Light,
        fontSize = 25.sp,
        modifier = Modifier
            .padding(20.dp)
    )

    if (myRoute.id != 0) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${myRoute.id} | ${myRoute.name}",
                    fontSize = 15.sp
                )
                Button(
                    onClick = {
                        navigateToMap(navController, myRoute)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.purple_custom)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 16.dp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

            }
        }
    } else {

        Text(
            text = stringResource(id = R.string.noCustomRoute),
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            fontFamily = fontNanum,
            modifier = Modifier
                .padding(20.dp)
        )

    }


}

fun navigateToMap(navController: NavController, route: MapRoute) {
    navController.currentBackStackEntry?.savedStateHandle?.set("ROUTE", route)
    navController.navigate(Routes.renderMap)
}

@Composable
fun GenericRoutes(allRoutes: List<MapRoute>, navController: NavController) {
    var nearbyBeacons by remember { mutableStateOf(emptyList<BeaconDto>()) }

    LaunchedEffect(Unit) {
        while (true) {
            nearbyBeacons = NearbyBeaconService.getNearbyBeacons()
                .mapNotNull { BeaconService.findByBeacon(it) }
            delay(1000L)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(20.dp, 0.dp)
    ) {

        Text(
            text = stringResource(id = R.string.titleGenericRoutes),
            fontWeight = FontWeight.Light,
            fontSize = 25.sp,
            modifier = Modifier
                .padding(20.dp, 10.dp)
        )
        Text(
            text = "(",
            fontWeight = FontWeight.Light,
            fontSize = 15.sp
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_beacons),
            contentDescription = null
        )
        Text(
            text = "Nearby)",
            fontWeight = FontWeight.Light,
            fontSize = 15.sp,
            modifier = Modifier
                .padding(10.dp)
        )


    }

    if (allRoutes.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            allRoutes.forEach { route ->

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${route.id} | ${route.name}",
                                fontSize = 15.sp
                            )

                            if (route.containsAnyBeacon(nearbyBeacons)) {
                                Icon(
                                    painter = painterResource(id = R.drawable.icon_beacons),
                                    contentDescription = "This route is nearby."
                                )
                            }

                            Button(
                                onClick = {
                                    navigateToMap(navController, route)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorResource(id = R.color.purple_custom)
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 16.dp
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                        }
                        Divider(color = Color.LightGray, thickness = 1.dp)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }

    } else {

        Text(
            text = stringResource(id = R.string.noGenericRoutes),
            fontWeight = FontWeight.Light,
            fontSize = 20.sp,
            fontFamily = fontNanum,
            modifier = Modifier
                .padding(20.dp)
        )

    }

}

@Composable
fun ContentIfNotLoggedMaps(navController: NavController) {
    Text(
        text = stringResource(id = R.string.msgNeedLogin),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light,
        fontSize = 25.sp,
        modifier = Modifier
            .padding(20.dp)
    )

    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )

    Button(
        onClick = { navController.navigate(Routes.login) },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.teal_700)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .height(55.dp)
    ) {
        Text(text = stringResource(id = R.string.titleLogin))
    }

    Button(
        onClick = { navController.navigate(Routes.signUp) },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.orange_autumn)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .height(55.dp)
    ) {
        Text(text = stringResource(id = R.string.titleSignUp))
    }
}