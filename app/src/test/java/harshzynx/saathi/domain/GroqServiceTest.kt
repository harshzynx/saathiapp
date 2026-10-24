package harshzynx.saathi.domain

import harshzynx.saathi.data.api.GroqApiService
import harshzynx.saathi.data.api.GroqChatRequest
import harshzynx.saathi.data.api.GroqChatResponse
import harshzynx.saathi.data.api.GroqChoice
import harshzynx.saathi.data.api.GroqMessage
import harshzynx.saathi.data.local.ApiKeyManager
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class GroqServiceTest {

    private lateinit var fakeGroqApiService: FakeGroqApiService
    private lateinit var fakeApiKeyManager: FakeApiKeyManager
    private lateinit var groqService: GroqService

    @Before
    fun setUp() {
        fakeGroqApiService = FakeGroqApiService()
        fakeApiKeyManager = FakeApiKeyManager()
        groqService = GroqService(fakeGroqApiService, OkHttpClient(), fakeApiKeyManager)
    }

    @Test
    fun testTwoDifferentUserMessagesReceiveDistinctRealResponses() = runBlocking {
        fakeApiKeyManager.key = "gsk_test_12345"

        fakeGroqApiService.customResponse = { request ->
            val userMsg = request.messages.last().content
            val aiText = "Response for '$userMsg'"
            Response.success(
                GroqChatResponse(
                    choices = listOf(
                        GroqChoice(message = GroqMessage(role = "assistant", content = aiText))
                    )
                )
            )
        }

        val res1 = groqService.getChatResponse("Hello, I feel stressed about exams")
        val res2 = groqService.getChatResponse("Can you give me a study tip?")

        assertEquals("Response for 'Hello, I feel stressed about exams'", res1)
        assertEquals("Response for 'Can you give me a study tip?'", res2)
        assertNotEquals(res1, res2)
        assertEquals(2, fakeGroqApiService.requestsReceived.size)
        assertEquals("Hello, I feel stressed about exams", fakeGroqApiService.requestsReceived[0].messages.last().content)
        assertEquals("Can you give me a study tip?", fakeGroqApiService.requestsReceived[1].messages.last().content)
    }
}

class FakeApiKeyManager : ApiKeyManager(
    context = NoOpContext()
) {
    var key: String = ""
    override fun getApiKey(): String = key
}

private class NoOpContext : android.content.ContextWrapper(null)
class FakeGroqApiService : GroqApiService {
    val requestsReceived = mutableListOf<GroqChatRequest>()
    var customResponse: ((GroqChatRequest) -> Response<GroqChatResponse>)? = null

    override suspend fun getChatCompletion(
        authHeader: String,
        request: GroqChatRequest
    ): Response<GroqChatResponse> {
        requestsReceived.add(request)
        return customResponse?.invoke(request)
            ?: Response.success(
                GroqChatResponse(
                    choices = listOf(
                        GroqChoice(message = GroqMessage(role = "assistant", content = "Default AI response"))
                    )
                )
            )
    }
}
