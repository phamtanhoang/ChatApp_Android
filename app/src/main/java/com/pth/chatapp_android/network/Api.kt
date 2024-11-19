package com.pth.chatapp_android.network

import com.google.gson.JsonObject
import com.pth.chatapp_android.model.MessageBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface Api {
    @POST("send")
    suspend fun sendMessage(
        @HeaderMap header:HashMap<String,String>,
        @Body messageBody: MessageBody
    ): Response<JsonObject>
}