package com.salastroya.bgandroid.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.getImagePainter
import com.salastroya.bgandroid.model.GardenNews
import com.salastroya.bgandroid.services.NewsService

@Composable
fun NewsDetailPage(newsId: Int, navController: NavController) {
    var gardenNews by remember { mutableStateOf<GardenNews?>(null) }

    LaunchedEffect(newsId) {
        gardenNews = NewsService.findById(newsId)
    }
    if (gardenNews != null) {
        NewsInfo(gardenNews)
    }
}

@Composable
fun NewsInfo(news: GardenNews?) {
    val context = LocalContext.current

    if (news != null) {
        LazyColumn(
            Modifier
                .fillMaxSize()
        ) {

            item {
                Image(
                    painter = getImagePainter(news.image, context = context),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.FillWidth
                )
            }

            item {
                Box(
                    modifier = Modifier.background(colorResource(id = R.color.teal_700))
                ) {
                    Text(
                        text = news.title,
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
                Text(
                    text = news.subtitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                )
            }

            item {
                Text(
                    text = news.getFormattedDate(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(start = 0.dp, top = 10.dp, end = 20.dp, bottom = 10.dp)
                        .fillMaxWidth()
                )
            }


            if (!news.description.isNullOrEmpty()) {
                item {
                    Text(
                        text = news.description,
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