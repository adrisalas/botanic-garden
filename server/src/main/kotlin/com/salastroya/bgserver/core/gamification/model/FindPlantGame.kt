package com.salastroya.bgserver.core.gamification.model

import java.time.LocalDateTime

data class FindPlantGame(
    val id: Int? = null,
    val plantId: Int,
    val start: LocalDateTime = LocalDateTime.now(),
    val end: LocalDateTime? = null
) {
    fun isActive(): Boolean {
        return end == null
    }
}
