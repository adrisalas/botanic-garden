package com.salastroya.bgandroid.services

import com.google.gson.Gson
import retrofit2.HttpException

private data class ErrorMessage(val reason: String)

fun HttpException.serverErrorMessage(): String {
    return this.response()
        ?.errorBody()
        ?.string()
        ?.let { Gson().fromJson(it, ErrorMessage::class.java).reason }
        ?: "Server error"
}