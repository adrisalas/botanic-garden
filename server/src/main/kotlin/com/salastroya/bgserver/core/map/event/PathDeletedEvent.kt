package com.salastroya.bgserver.core.map.event

import org.springframework.context.ApplicationEvent

data class PathDeletedEvent(
    private var source: Any,
    val pointAId: Int,
    val pointBId: Int
) : ApplicationEvent(source)
