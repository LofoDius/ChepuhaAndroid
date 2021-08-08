package com.lofod.chepuha.model.request

import com.lofod.chepuha.model.Answer
import kotlinx.serialization.Serializable

@Serializable
data class AnswerRequest(
    val answer: Answer,
    val gameCode: String
)
