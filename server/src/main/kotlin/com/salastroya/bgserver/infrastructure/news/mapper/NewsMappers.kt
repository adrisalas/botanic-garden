package com.salastroya.bgserver.infrastructure.news.mapper

import com.salastroya.bgserver.core.news.model.News
import com.salastroya.bgserver.infrastructure.news.dto.NewsDto

fun NewsDto.toModel(): News {
    return News(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        description = this.description,
        date = this.date,
        image = this.image
    )
}

fun News.toDto(): NewsDto {
    return NewsDto(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        description = this.description,
        date = this.date,
        image = this.image
    )
}