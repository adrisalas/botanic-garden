package com.salastroya.bgandroid.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.salastroya.bgandroid.BotanicGardenApplication
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.services.auth.JWTService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfilePage(navController: NavController) {
    val context = LocalContext.current
    val tokenJWT = remember { JWTService.jwtStore }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titleProfile),
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp),
                color = Color.White
            )
        }

        if (tokenJWT.isNullOrEmpty()) {
            contentIfNotLogged(navController = navController)
        } else {
            contentIfLogged(navController = navController, context)
        }

    }
}

@Composable
fun contentIfLogged(navController: NavController, context: Context) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .size(width = 150.dp, height = 150.dp)
                .padding(5.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar_default),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .background(Color.Transparent),
                contentDescription = "Avatar Default"
            )
        }

        Text(
            text = JWTService.getUserName(),
            fontWeight = FontWeight.Light,
            fontSize = 30.sp,
            modifier = Modifier
                .width(170.dp)
                .padding(horizontal = 30.dp, vertical = 0.dp)
        )
    }

    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
    )

    Button(
        onClick = { /*TODO*/ },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.orange_autumn)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp)
            .height(55.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.key),
            contentDescription = "",
            modifier = Modifier.padding(10.dp, 0.dp)
        )
        Text(text = stringResource(id = R.string.btnChangePassword))
    }

    Button(
        onClick = {
            JWTService.clearJwt(context = BotanicGardenApplication.instance)
            navController.navigate(Routes.home)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.dark_red)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 16.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
            .height(55.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logout_icon),
            contentDescription = "",
            modifier = Modifier.padding(10.dp, 0.dp)
        )
        Text(text = stringResource(id = R.string.btnLogout))
    }
}

@Composable
fun contentIfNotLogged(navController: NavController) {
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