package com.salastroya.bgandroid.services.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object JWTService {
    private const val JWT_KEY = "jwt"
    lateinit var sharedPreferences: SharedPreferences
    var jwtStore by mutableStateOf("")
        private set

    fun getJwtFromPreferences(context: Context): String {
        return sharedPreferences.getString(JWT_KEY, "") ?: ""
    }

    fun storeJwt(context: Context, jwt: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(JWT_KEY, jwt)
        editor.apply()
        jwtStore = jwt
    }

    fun clearJwt(context: Context) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(JWT_KEY)
        editor.apply()
        jwtStore = ""
    }

    fun getUserName(): String {
        return "Stella"
    }
}