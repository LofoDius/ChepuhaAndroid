package com.lofod.chepuha.model.response

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val code: Int,
    val question: String,
    val questionNumber: Int
)
