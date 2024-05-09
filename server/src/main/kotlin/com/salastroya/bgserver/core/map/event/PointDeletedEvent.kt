package com.salastroya.bgserver.core.map.event

import org.springframework.context.ApplicationEvent

data class PointDeletedEvent(
    private var source: Any,
    val id: Int
) : ApplicationEvent(source)