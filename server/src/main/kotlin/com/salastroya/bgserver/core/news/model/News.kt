package com.salastroya.bgserver.core.news

import java.time.LocalDateTime

data class GardenNews(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val image: String? = null
)