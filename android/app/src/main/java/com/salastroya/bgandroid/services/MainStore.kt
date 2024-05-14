package com.salastroya.bgandroid.services

import androidx.compose.runtime.mutableStateOf

object MainStore {

    val bloomingFilter = mutableStateOf(false)

    val searchText = mutableStateOf("")

    val activeSearch = mutableStateOf(true)

}