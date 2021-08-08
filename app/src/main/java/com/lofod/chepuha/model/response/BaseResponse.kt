package com.lofod.chepuha.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse(
    val code: Int
)