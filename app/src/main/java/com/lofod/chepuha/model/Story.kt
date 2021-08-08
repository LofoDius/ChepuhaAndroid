package com.lofod.chepuha.model

import kotlinx.serialization.Serializable

@Serializable
data class Story(
    val answers: List<Answer>
)
