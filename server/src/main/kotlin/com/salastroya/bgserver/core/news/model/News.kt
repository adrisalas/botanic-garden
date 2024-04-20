package com.salastroya.bgserver.core.news.model

import java.time.LocalDateTime

data class News(
    val id: Int?,
    val title: String,
    val subtitle: String,
    val description: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val image: String? = null
)