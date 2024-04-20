package com.salastroya.bgandroid.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.ui.theme.fontDancing

@Composable
fun HomePage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.jardinbotanicogijon),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.4f) })
            )

            LazyColumn(
                modifier = Modifier.matchParentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.titleHome),
                        fontSize = 90.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 80.sp,
                        fontFamily = fontDancing
                    )
                    Text(
                        text = stringResource(id = R.string.cityHome),
                        fontSize = 50.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontFamily = fontDancing
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
                            .padding(horizontal = 40.dp, vertical = 0.dp)
                    ) {
                        Text(text = stringResource(id = R.string.titleLogin))
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }

            }

        }
    }
}