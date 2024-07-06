package com.salastroya.bgandroid.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.BotanicGardenApplication
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.LoadingSpinner
import com.salastroya.bgandroid.components.SmallCard
import com.salastroya.bgandroid.components.getImagePainter
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.MainStore
import com.salastroya.bgandroid.services.NearbyPlantService
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.services.beacons.BeaconRangerService
import com.salastroya.bgandroid.ui.theme.fontNanum
import kotlinx.coroutines.delay
import org.altbeacon.beacon.BeaconManager
import kotlin.math.pow
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NearbyPlantsPage(navController: NavController) {
    val context = LocalContext.current
    var nearbyPlants by remember { mutableStateOf(emptyList<Pair<Plant, Double>>()) }
    val (activeSearch, setActiveSearch) = MainStore.activeSearch

    LaunchedEffect(Unit) {
        while (true) {
            nearbyPlants = NearbyPlantService.getNearbyPlants()
            delay(1000L)
        }
    }

    LaunchedEffect(activeSearch) {
        if (!activeSearch) {
            BeaconRangerService.stopBeaconScanning()
            return@LaunchedEffect
        }
        val permissionsGranted = BeaconScanPermissionsActivity.allPermissionsGranted(context, true)

        if (!permissionsGranted) {
            setActiveSearch(false)
            askPermissions(context)
        }

        if (BeaconManager.getInstanceForApplication(BotanicGardenApplication.instance).monitoredRegions.isEmpty()) {
            BeaconRangerService.startBeaconScanning()
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.teal_700)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.titleNearby),
                        fontWeight = FontWeight.Light,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(20.dp),
                        color = Color.White
                    )
                    CustomSwitch(activeSearch, setActiveSearch)
                }
            }

        }
    ) {
        if (!activeSearch || nearbyPlants.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!activeSearch) {
                        Text(
                            text = stringResource(R.string.scanningDisabled),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = fontNanum
                        )
                    } else {
                        LoadingSpinner(stringResource(R.string.scanning))
                    }
                }
            }

        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }

                item() {
                    FlowRow(modifier = Modifier.fillMaxSize()) {
                        nearbyPlants.forEach { (plant, distance) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(10.dp)
                                    .clickable {
                                        navController.navigate(Routes.plantDetail + "/" + plant.id)
                                    }
                            ) {
                                SmallCard(
                                    painter = getImagePainter(plant.image, context),
                                    title = plant.commonName,
                                    description = "${distance.roundToDecimal(2)} m from you",
                                    icon = Icons.Default.LocationOn,
                                    iconColor = colorResource(id = R.color.mint_green)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

        }
    }

}

@Composable
fun CustomSwitch(checked: Boolean, setChecked: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = setChecked,
        colors = SwitchDefaults.colors(
            checkedThumbColor = colorResource(id = R.color.teal_700),
            checkedTrackColor = colorResource(id = R.color.mint_green),
            uncheckedThumbColor = colorResource(id = R.color.inactive),
            uncheckedTrackColor = Color.LightGray,
            uncheckedBorderColor = colorResource(id = R.color.inactive)
        ),
        thumbContent = {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                    tint = colorResource(id = R.color.mint_green)

                )
            }
        }
    )
}

private fun Double.roundToDecimal(decimals: Int): Double {
    val multiplier = 10.0.pow(decimals.toDouble())
    return (this * multiplier).roundToInt() / multiplier
}