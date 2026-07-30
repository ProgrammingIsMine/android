package com.example.myapplicationtrendprice.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class ChatRequest(
    val message: String,
    val user_data: Map<String, @JvmSuppressWildcards Any?>? = null
)

data class ChatResponse(
    val answer: String? = null,
    val actions: List<ChatAction> = emptyList()
)

data class ChatAction(
    val type: String,
    val payload: Any? = null
)

interface AiApi {
    @POST("ai-service/external/chat")
    suspend fun externalChat(@Body request: ChatRequest): ChatResponse
}