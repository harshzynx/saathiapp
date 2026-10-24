package harshzynx.saathi.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroqChatRequest(
    @Json(name = "messages") val messages: List<GroqMessage>,
    @Json(name = "model") val model: String,
    @Json(name = "temperature") val temperature: Double = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int = 800
)

@JsonClass(generateAdapter = true)
data class GroqMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class GroqChatResponse(
    @Json(name = "choices") val choices: List<GroqChoice>?
)

@JsonClass(generateAdapter = true)
data class GroqChoice(
    @Json(name = "message") val message: GroqMessage?
)
