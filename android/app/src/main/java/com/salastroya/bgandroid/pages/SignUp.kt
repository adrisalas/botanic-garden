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
import androidx.compose.ui.text.font.FontWeight
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
fun contentSignUp(navController: NavController) {
    val context = LocalContext.current
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPasword) = remember { mutableStateOf("") }
    val (repeatedPassword, setRepeatedPass) = remember { mutableStateOf("") }
    val (supportingText, setSupportingText) = remember { mutableStateOf("") }

    val (isErrorUsername, setErrorUsername) = remember { mutableStateOf(false) }
    val (isErrorPasswd, setErrorPasswd) = remember { mutableStateOf(false) }
    val (isErrorPasswd2, setErrorPasswd2) = remember { mutableStateOf(false) }

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
                    .padding(0.dp, 20.dp),
                isError = isErrorUsername
            )

            PasswordField(
                value = password,
                onChange = { passwd: String -> setPasword(passwd) },
                submit = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                isError = isErrorPasswd
            )

            PasswordField(
                value = repeatedPassword,
                onChange = { passwd: String -> setRepeatedPass(passwd) },
                submit = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 20.dp),
                label = stringResource(id = R.string.repeatPassword),
                placeholder = stringResource(id = R.string.password2Placeholder),
                isError = isErrorPasswd2,
                supportingText = supportingText
            )

            Button(
                onClick = {
                    runBlocking {
                        callSignUpService(
                            username = username,
                            password = password,
                            repeatedPassword = repeatedPassword,
                            context = context,
                            navController = navController,
                            setErrorUserName = setErrorUsername,
                            setErrorPasswd = setErrorPasswd,
                            setErrorPasswd2 = setErrorPasswd2,
                            setSupportingText = setSupportingText
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

suspend fun callSignUpService(
    username: String,
    password: String,
    repeatedPassword: String,
    context: Context,
    navController: NavController,
    setErrorUserName: (error: Boolean) -> Unit,
    setErrorPasswd: (error: Boolean) -> Unit,
    setErrorPasswd2: (error: Boolean) -> Unit,
    setSupportingText: (error: String) -> Unit
) {
    setErrorUserName(false)
    setErrorPasswd(false)
    setErrorPasswd2(false)

    val toast = Toast.makeText(context, "", Toast.LENGTH_SHORT)
    toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 50)

    val fieldsAreValid = validFields(
        username = username,
        password = password,
        repeatedPassword = repeatedPassword,
        setErrorUserName,
        setErrorPasswd,
        setErrorPasswd2,
        setSupportingText
    )

    if (!fieldsAreValid.second) {
        if (fieldsAreValid.first.isNotEmpty()) {
            toast.setText(fieldsAreValid.first)
            toast.show()
        }
        return
    }

    try {
        AuthService.singUp(username, password)
        toast.setText("Account created successfully.")
        navController.navigate(Routes.login)
    } catch (ex: HttpException) {
        toast.setText(ex.serverErrorMessage())
    }

    toast.show()
}

fun validFields(
    username: String,
    password: String,
    repeatedPassword: String,
    setErrorUserName: (error: Boolean) -> Unit,
    setErrorPasswd: (error: Boolean) -> Unit,
    setErrorPasswd2: (error: Boolean) -> Unit,
    setSupportingText: (text: String) -> Unit
): Pair<String, Boolean> {
    setErrorPasswd(false)
    setErrorUserName(false)
    setErrorPasswd2(false)
    setSupportingText("")

    val regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^\\-_&+=!])(?=\\S+$).{8,}$".toRegex()

    if (username.isEmpty() || password.isEmpty() || repeatedPassword.isEmpty()) {
        setErrorUserName(username.isEmpty())
        setErrorPasswd(password.isEmpty())
        setErrorPasswd2(repeatedPassword.isEmpty())
        return Pair("All fields are required", false)
    }

    if (password != repeatedPassword) {
        setErrorPasswd(true)
        setErrorPasswd2(true)
        return Pair("Passwords do not match", false)
    }

    if (!regex.matches(password) || !regex.matches(repeatedPassword)) {
        val msg =
            setErrorPasswd(true)
        setErrorPasswd2(true)
        setSupportingText("Password must be at least 8 characters, a capital letter, a number and a special character")
        return Pair(
            "",
            false
        )
    }

    return Pair("", true)
}