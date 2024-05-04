package com.salastroya.bgandroid.controller

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.salastroya.bgandroid.R


fun imageIsUrl(image: String?): Boolean {
    return !image.isNullOrEmpty() && (image.contains("https") || image.contains("www") || image.contains(
        ".com"
    ))
}

@Composable
fun returnPainterImg(image: String?, context: Context): Painter {
    if (imageIsUrl(image)) {
        return rememberAsyncImagePainter(model = image)
    } else {
        var imageResourceId: Int = 0
        var painterReturn = painterResource(id = R.drawable.image_not_found)
        if (!image.isNullOrEmpty()) {
            imageResourceId = remember(image) {
                context.resources.getIdentifier(
                    image,
                    "drawable",
                    context.packageName
                )
            }
        }

        if (imageResourceId != 0) {
            painterReturn = painterResource(id = imageResourceId)
        }
        return painterReturn
    }
}