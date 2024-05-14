package com.salastroya.bgandroid.pages

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.LoadingSpinner
import com.salastroya.bgandroid.model.Item
import com.salastroya.bgandroid.model.ItemType
import com.salastroya.bgandroid.model.POI
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.POIsService
import com.salastroya.bgandroid.services.PlantService
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.services.UserRouteService
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

@Composable
fun MapsCustomRoutePage(navController: NavController) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var checkBoxStatesPlants = remember { mutableStateListOf<Boolean>() }
    var checkBoxStatesPOIs = remember { mutableStateListOf<Boolean>() }
    var allPlants by remember { mutableStateOf(emptyList<Plant>()) }
    var allPOIs by remember { mutableStateOf(emptyList<POI>()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loading = true
        allPlants = PlantService.findAll()
        allPOIs = POIsService.findAll()
        loading = false
    }

    allPlants.forEach { _ ->
        checkBoxStatesPlants.add(false)
    }

    allPOIs.forEach { _ ->
        checkBoxStatesPOIs.add(false)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { focusManager.clearFocus() }
            )
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titleCreateRoute),
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp),
                color = Color.White
            )
        }

        if (loading) {
            LoadingSpinner("Loading...")
        } else {
            FormCreateRoute(
                navController,
                allPlants,
                checkBoxStatesPlants,
                allPOIs,
                checkBoxStatesPOIs,
                context
            )
        }

    }
}

@Composable
fun FormCreateRoute(
    navController: NavController,
    allPlants: List<Plant>,
    checkBoxStatesPlants: SnapshotStateList<Boolean>,
    allPOIs: List<POI>,
    checkBoxStatesPOIs: SnapshotStateList<Boolean>,
    context: Context
) {
    Button(
        onClick = {
            runBlocking {
                requestCreateRoute(
                    allPlants,
                    checkBoxStatesPlants,
                    allPOIs,
                    checkBoxStatesPOIs,
                    context,
                    navController
                )
            }
        },
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
        Text(text = stringResource(id = R.string.buttonCreateRoute))
    }

    FormPOIs(allPOIs, checkBoxStatesPOIs)
    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )
    FormPlants(allPlants = allPlants, checkBoxStates = checkBoxStatesPlants)
    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )

}

suspend fun requestCreateRoute(
    allPlants: List<Plant>,
    checkBoxStatesPlants: SnapshotStateList<Boolean>,
    allPOIs: List<POI>,
    checkBoxStatesPOIs: SnapshotStateList<Boolean>,
    context: Context,
    navController: NavController
) {
    val toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 50)
    var listOfItems: MutableList<Item> = mutableListOf()

    checkBoxStatesPOIs.forEachIndexed { index, isChecked ->
        if (isChecked) {
            listOfItems.add(Item(ItemType.POI, allPOIs[index].id))
        }
    }

    checkBoxStatesPlants.forEachIndexed { index, isChecked ->
        if (isChecked) {
            listOfItems.add(Item(ItemType.PLANT, allPlants[index].id))
        }
    }

    if (listOfItems.size >= 2) {
        try {
            val mapRoute = UserRouteService.requestMyRoute(listOfItems);
            if (mapRoute.id == 0) {
                toast.setText("Route was not created. An error occurred.")
            } else {
                toast.setText("Route created successfully")
                navController.navigate(Routes.map)
            }
        } catch (ex: HttpException) {
            toast.setText("An error occurred creating custom route.")
        } finally {
            toast.show()
        }
    } else {
        toast.setText("Please select at least 2 items and route name is required.")
        toast.show()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormPlants(allPlants: List<Plant>, checkBoxStates: SnapshotStateList<Boolean>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
                text = stringResource(id = R.string.subtitleCreateRoutePlants),
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
        }

        item {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                allPlants.forEachIndexed { index, plant ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(20.dp, 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.leaf),
                            tint = colorResource(id = R.color.teal_200),
                            contentDescription = null
                        )
                        Text(text = "${plant.commonName}")
                        Checkbox(
                            checked = checkBoxStates[index],
                            onCheckedChange = { isChecked ->
                                checkBoxStates[index] = isChecked
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormPOIs(allPOIs: List<POI>, checkBoxStates: SnapshotStateList<Boolean>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Text(
                text = stringResource(id = R.string.subtitleCreateRoutePOIs),
                fontWeight = FontWeight.Light,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(20.dp)
            )
        }

        item {
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                allPOIs.forEachIndexed { index, poi ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(20.dp, 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            tint = colorResource(id = R.color.purple_500),
                            contentDescription = null
                        )
                        Text(text = "${poi.name}")
                        Checkbox(
                            checked = checkBoxStates[index],
                            onCheckedChange = { isChecked ->
                                checkBoxStates[index] = isChecked
                            }
                        )
                    }
                }
            }
        }
    }
}