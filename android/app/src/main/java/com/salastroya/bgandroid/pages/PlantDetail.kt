package com.salastroya.bgandroid.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.model.GardenIcon
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.PlantService
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlantDetailPage(plantId: Int, navController: NavController) {
    var plant by remember { mutableStateOf<Plant?>(null) }

    LaunchedEffect(plantId) {
        plant = PlantService.findById(plantId)
    }
    if (plant != null) {
        PlantInfo(plant)
    }
}

@Composable
fun PlantInfo(plant: Plant?) {
    val context = LocalContext.current

    if (plant != null) {
        LazyColumn(
            Modifier.fillMaxSize()
        ) {

            item {
                val imageResourceId = remember(plant.image) {
                    context.resources.getIdentifier(
                        plant.image,
                        "drawable",
                        context.packageName
                    )
                }

                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }

            item {
                Box(
                    modifier = Modifier.background(colorResource(id = R.color.teal_700))
                ) {
                    Text(
                        text = plant.commonName + " / " + plant.scientificName,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }
            }

            item {
                val firstMonth =
                    plant.details?.flowering?.first?.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )
                val lastMonth =
                    plant.details?.flowering?.second?.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )
                val plantDetails = plant.details

                PlantDetail(plantDetails?.season?.getIcon(), plantDetails?.season?.seasonName)
                PlantDetail(
                    plantDetails?.getIconDetail(stringResource(id = R.string.leafIcon)),
                    plantDetails?.leafType
                )
                PlantDetail(
                    plantDetails?.getIconDetail(stringResource(id = R.string.waterIcon)),
                    plantDetails?.water
                )
                PlantDetail(
                    plantDetails?.getIconDetail(stringResource(id = R.string.floweringIcon)),
                    "Flowering months: $firstMonth/$lastMonth"
                )
            }

            if (plant.description != null) {
                item {
                    Text(
                        text = plant.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}

@Composable
fun PlantDetail(iconInfo: GardenIcon?, text: String?) {
    if (text == null || iconInfo == null) {
        return
    }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(10.dp),
            fontSize = 18.sp

        )

        Icon(
            painter = painterResource(id = iconInfo.iconId),
            tint = colorResource(id = iconInfo.colorId),
            contentDescription = null
        )
    }

}