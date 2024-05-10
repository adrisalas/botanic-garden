package com.salastroya.bgserver.core.poi.event

import org.springframework.context.ApplicationEvent

data class PoiDeletedEvent(
    private var source: Any,
    val id: Int,
) : ApplicationEvent(source)