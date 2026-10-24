package harshzynx.saathi.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("openai/v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}
