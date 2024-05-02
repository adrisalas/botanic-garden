package com.salastroya.bgserver.core.map.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Route(
    val id: Int?,
    val name: String,
    val points: List<Point>,
    @JsonIgnore
    val associatedUser: String? = null
)
