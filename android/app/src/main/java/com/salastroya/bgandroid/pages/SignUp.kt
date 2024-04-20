package com.salastroya.bgandroid.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.salastroya.bgandroid.R
import com.salastroya.bgandroid.components.LoginField
import com.salastroya.bgandroid.components.PasswordField
import com.salastroya.bgandroid.services.MainStore
import com.salastroya.bgandroid.services.Routes

@Composable
fun contentSignUp(navController: NavController){
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPasword) = remember { mutableStateOf("") }
    val (repeatedPassword, setRepeatedPass) = remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titleSignUp),
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                modifier = Modifier
                    .padding(20.dp),
                color = Color.White
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginField(
                value = username,
                onChange = { username: String -> setUsername(username) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)
            )

            PasswordField(
                value = password,
                onChange = { passwd: String -> setPasword(passwd) },
                submit = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp)
            )

            PasswordField(
                value = repeatedPassword,
                onChange = { passwd: String -> setRepeatedPass(passwd) },
                submit = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                label = stringResource(id = R.string.repeatPassword),
                placeholder = stringResource(id = R.string.password2Placeholder)
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
                    .padding(horizontal = 40.dp, vertical = 20.dp)
            ) {
                Text(text = stringResource(R.string.submitButton))
            }
        }


    }
}