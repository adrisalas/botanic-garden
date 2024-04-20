package com.salastroya.bgandroid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.salastroya.bgandroid.R

@Composable
fun SharedHeader(title: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = title,
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                modifier = Modifier.padding(20.dp),
                color = Color.White
            )
        }
    }
}

@Composable
fun ButtonShared(
    icon: ImageVector,
    onClick: () -> Unit
) {
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        contentAlignment = Alignment.TopEnd
    ) {
        ElevatedButton(
            onClick = onClick,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .padding(20.dp)
                .offset {
                    IntOffset(x = +offsetInPx, y = -offsetInPx)
                }
                .shadow(20.dp, RoundedCornerShape(16.dp), ambientColor = Color.Black),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.teal_700)),
            contentPadding = PaddingValues(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Search for near beacons"
            )
        }
    }
}
