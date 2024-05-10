package com.salastroya.bgserver.core.plant.event

import org.springframework.context.ApplicationEvent

data class PlantDeletedEvent(
    private var source: Any,
    val id: Int,
) : ApplicationEvent(source)