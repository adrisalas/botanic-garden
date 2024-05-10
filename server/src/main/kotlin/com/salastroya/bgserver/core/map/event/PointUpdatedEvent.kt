package com.salastroya.bgserver.core.map.event

import org.springframework.context.ApplicationEvent

data class PointUpdatedEvent(
    private var source: Any,
    val id: Int
) : ApplicationEvent(source)