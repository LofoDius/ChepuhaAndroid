package com.lofod.chepuha.model

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val questionNumber: Int,
    val text: String,
    val author: String
)