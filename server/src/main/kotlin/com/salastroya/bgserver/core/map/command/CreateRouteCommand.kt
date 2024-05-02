package com.salastroya.bgserver.core.map.command

data class CreateRouteCommand(
    val name: String,
    val points: List<Int>
)
