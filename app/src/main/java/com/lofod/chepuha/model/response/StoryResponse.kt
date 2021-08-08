package com.lofod.chepuha.model.response

import com.lofod.chepuha.model.Story
import kotlinx.serialization.Serializable

@Serializable
data class StoryResponse(
    val code: Int,
    val story: Story
)
