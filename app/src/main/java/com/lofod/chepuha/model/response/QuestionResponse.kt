package com.lofod.chepuha.model.response

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val question: String,
    val questionNumber: Int
)
