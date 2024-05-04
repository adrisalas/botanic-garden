package com.salastroya.bgandroid.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material3.Scaffold
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
import com.salastroya.bgandroid.components.LargeGenericCard
import com.salastroya.bgandroid.components.LoadingSpinner
import com.salastroya.bgandroid.model.GardenNews
import com.salastroya.bgandroid.services.NewsService
import com.salastroya.bgandroid.services.Routes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsPage(navController: NavController) {
    val focusManager = LocalFocusManager.current
    var loading by remember { mutableStateOf(false) }
    var allNews by remember { mutableStateOf(emptyList<GardenNews>()) }

    LaunchedEffect(Unit) {
        loading = true
        allNews = NewsService.findAll()
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
                        text = stringResource(id = R.string.titleNews),
                        fontWeight = FontWeight.Light,
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(20.dp),
                        color = Color.White
                    )
                }
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
                    Spacer(modifier = Modifier.height(90.dp))
                }
                items(
                    allNews
                ) { news ->
                    LargeGenericCard(
                        title = news.title,
                        subtitle = news.subtitle,
                        navController = navController,
                        image = painterResource(id = R.drawable.calendar_page),
                        date = news.getFormattedDate(),
                        route = Routes.newsDetail + "/" + news.id
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

}