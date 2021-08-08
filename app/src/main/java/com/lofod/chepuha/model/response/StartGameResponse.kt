package com.lofod.chepuha.model.response

import kotlinx.serialization.Serializable

@Serializable
data class StartGameResponse(
    val code: Int,
    val gameCode: String
)
