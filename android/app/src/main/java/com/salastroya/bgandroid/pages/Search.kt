package com.salastroya.bgandroid.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.LargeCard
import com.salastroya.bgandroid.components.LoadingSpinner
import com.salastroya.bgandroid.model.Plant
import com.salastroya.bgandroid.services.MainStore
import com.salastroya.bgandroid.services.PlantService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchPage(navController: NavController) {
    val focusManager = LocalFocusManager.current
    val (searchText, setSearchText) = remember { MainStore.searchText }
    val bloomingFilter by remember { MainStore.bloomingFilter }
    var filteredPlants by remember { mutableStateOf(emptyList<Plant>()) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(searchText, bloomingFilter) {
        loading = true
        filteredPlants = PlantService.findAll()
            .filter { it.doesMatchSearchQuery(searchText) }
            .filter { it.doesMatchBloomingFilter(bloomingFilter) }
        loading = false
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
                        text = stringResource(id = R.string.titleSearch),
                        fontWeight = FontWeight.Light,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(20.dp),
                        color = Color.White
                    )
                    GetButtonIfBlooming()
                }

                TextField(
                    value = searchText,
                    onValueChange = { text: String -> setSearchText(text) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(text = stringResource(id = R.string.placeholderSearch)) },
                    trailingIcon = {
                        if (searchText.isBlank()) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        } else {
                            Icon(Icons.Default.Clear,
                                contentDescription = "Clear Text",
                                modifier = Modifier
                                    .clickable {
                                        setSearchText("")
                                    }
                            )
                        }

                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.light_gray),
                        unfocusedContainerColor = colorResource(id = R.color.light_gray),
                        disabledContainerColor = colorResource(id = R.color.inactive),
                        focusedIndicatorColor = colorResource(id = R.color.teal_200),
                    )
                )
            }

        },
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { focusManager.clearFocus() }
            )
        }
    ) {
        if (loading) {
            LoadingSpinner("Loading...")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Spacer(modifier = Modifier.height(150.dp))
                }
                items(
                    filteredPlants
                ) { plant ->
                    LargeCard(
                        plant = plant,
                        navController = navController
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

}

@Composable
fun GetButtonIfBlooming() {
    val (blooming, setBlooming) = MainStore.bloomingFilter
    val buttonText =
        if (!blooming) stringResource(id = R.string.buttonBloomingOff) else stringResource(id = R.string.buttonBloomingOn)
    val contentColor =
        if (!blooming) R.color.white else R.color.inactive
    val containerColor = if (!blooming) colorResource(id = R.color.teal_500) else Color.White

    OutlinedButton(
        onClick = { setBlooming(!blooming) },
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = colorResource(contentColor),
        ),
        border = BorderStroke(1.dp, color = colorResource(id = contentColor))
    ) {
        Text(buttonText)
        Icon(
            painter = painterResource(id = R.drawable.blooming),
            contentDescription = null,
            tint = colorResource(contentColor)
        )
    }
}