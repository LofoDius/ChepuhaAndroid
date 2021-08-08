package com.lofod.chepuha.model.request

import com.lofod.chepuha.model.Player
import kotlinx.serialization.Serializable

@Serializable
data class StartGameRequest(
    val starter: Player
)
