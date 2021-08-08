package com.lofod.chepuha.retrofit

import com.lofod.chepuha.model.Player
import com.lofod.chepuha.model.request.AnswerRequest
import com.lofod.chepuha.model.request.ConnectToGameRequest
import com.lofod.chepuha.model.request.StartGameRequest
import com.lofod.chepuha.model.request.StoryRequest
import com.lofod.chepuha.model.response.BaseResponse
import com.lofod.chepuha.model.response.StartGameResponse
import com.lofod.chepuha.model.response.StoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface API {

    @POST("create")
    fun createGame(@Body request: StartGameRequest): Call<StartGameResponse>

    @POST("/start/{gameCode}")
    fun startGame(@Path("gameCode") gameCode: String): Call<BaseResponse>

    @POST("connect")
    fun connectToGame(@Body request: ConnectToGameRequest): Call<BaseResponse>

    @POST("connectedPlayer/{gameCode}")
    fun getConnectedPlayer(@Path("gameCode") gameCode: String): Call<List<Player>>

    @POST("message")
    fun sendMessage(@Body request: AnswerRequest): Call<BaseResponse>

    @POST("story")
    fun getStory(@Body request: StoryRequest): Call<StoryResponse>
}