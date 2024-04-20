package com.salastroya.bgserver.infrastructure.news.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("news")
data class NewsDto(
    @Id val id: Int?,
    val title: String,
    val subtitle: String,
    val description: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val image: String? = null
)