package com.salastroya.bgandroid.pages

import android.content.Context
import android.view.Gravity
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.salastroya.bgandroid.services.Routes
import com.salastroya.bgandroid.services.auth.AuthService
import com.salastroya.bgandroid.services.serverErrorMessage
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException


@Composable
fun ContentLogin(navController: NavController) {
    val context = LocalContext.current
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (isErrorUsername, setErrorUsername) = remember { mutableStateOf(false) }
    val (isErrorPasswd, setErrorPasswd) = remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.teal_700)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.titleLogin),
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
                    .padding(0.dp, 20.dp),
                isError = isErrorUsername
            )
            PasswordField(
                value = password,
                onChange = { passwd: String -> setPassword(passwd) },
                submit = {
                    runBlocking {
                        callLoginService(
                            username,
                            password,
                            context,
                            navController,
                            setErrorUsername,
                            setErrorPasswd
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                isError = isErrorPasswd
            )

            val linkText = stringResource(id = R.string.textToSignUp)
            val textLength = linkText.length
            ClickableText(
                text =
                AnnotatedString(
                    text = linkText,
                    spanStyles = listOf(
                        AnnotatedString.Range(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            0,
                            textLength
                        ),
                        AnnotatedString.Range(
                            SpanStyle(textDecoration = TextDecoration.Underline),
                            0,
                            textLength
                        ),
                        AnnotatedString.Range(
                            SpanStyle(color = Color.Blue),
                            0,
                            textLength
                        )
                    )
                ),
                onClick = {
                    navController.navigate(Routes.signUp)
                })

            Button(
                onClick = {
                    runBlocking {
                        callLoginService(
                            username,
                            password,
                            context,
                            navController,
                            setErrorUsername,
                            setErrorPasswd
                        )
                    }
                },
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

suspend fun callLoginService(
    username: String,
    password: String,
    context: Context,
    navController: NavController,
    setErrorUserName: (error: Boolean) -> Unit,
    setErrorPasswd: (error: Boolean) -> Unit
) {
    setErrorPasswd(false)
    setErrorUserName(false)

    val toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 50)

    if (username.isEmpty() || password.isEmpty()) {
        toast.setText("Please enter your username and password.")
        if (username.isEmpty()) {
            setErrorUserName(true)
        }
        if (password.isEmpty()) {
            setErrorPasswd(true)
        }
        return
    }

    try {
        AuthService.login(username, password)
        toast.setText("Login successful")
        navController.navigate(Routes.home)

    } catch (ex: HttpException) {
        toast.setText(ex.serverErrorMessage())
    } finally {
        toast.show()
    }

}