package com.salastroya.bgserver.core.map.command

import com.salastroya.bgserver.core.map.model.Item

data class RequestRouteCommand(
    val username: String,
    val items: List<Item>
)