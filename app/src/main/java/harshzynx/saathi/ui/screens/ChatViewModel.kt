package harshzynx.saathi.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.api.GroqMessage
import harshzynx.saathi.data.model.ChatMessage
import harshzynx.saathi.data.repository.WellbeingRepository
import harshzynx.saathi.domain.GroqService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

data class Message(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val groqService: GroqService,
    private val wellbeingRepository: WellbeingRepository
) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> = _messages

    var isTyping by mutableStateOf(false)
        private set

    var isLoadingHistory by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    var isJustListen by mutableStateOf(false)

    var selectedCategory by mutableStateOf<String?>(null)

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            try {
                val history = wellbeingRepository.getChatHistory()

                _messages.clear()

                if (history.isNotEmpty()) {
                    _messages.addAll(
                        history.map {
                            Message(
                                text = it.text,
                                isUser = it.isUser,
                                timestamp = it.timestamp
                            )
                        }
                    )
                } else {
                    _messages.add(
                        Message(
                            text = "Namaste! Main Saathi hoon — Your Safe Friend 🌱. Aaj man kaisa hai? Kuch share karna chahoge?",
                            isUser = false
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load chat history", e)
                errorMessage = "Chat history load nahi ho paayi."

                if (_messages.isEmpty()) {
                    _messages.add(
                        Message(
                            text = "Namaste! Main Saathi hoon 🌱. Main yahan hoon aapke saath.",
                            isUser = false
                        )
                    )
                }
            } finally {
                isLoadingHistory = false
            }
        }
    }

    fun sendMessage(text: String) {
        val cleanedText = text.trim()

        if (cleanedText.isBlank()) return
        if (isLoadingHistory || isTyping) return

        errorMessage = null

        val userMessage = Message(
            text = cleanedText,
            isUser = true
        )

        _messages.add(userMessage)
        isTyping = true

        viewModelScope.launch {
            try {
                // Save user message to local & cloud repository
                wellbeingRepository.saveChatMessage(
                    ChatMessage(
                        text = cleanedText,
                        isUser = true,
                        timestamp = userMessage.timestamp
                    )
                )

                // Build chronological history for context (exclude greeting & loading typing indicators)
                val historyGroqMessages = _messages
                    .filter { it.text.isNotBlank() && it.text != "..." && !it.text.startsWith("Namaste! Main Saathi") && !it.text.startsWith("⚠️") }
                    .dropLast(1) // exclude current new userMessage which is passed as userMessage
                    .map {
                        GroqMessage(
                            role = if (it.isUser) "user" else "assistant",
                            content = it.text
                        )
                    }

                // Fetch real response from Groq AI
                val responseContent = groqService.getChatResponse(
                    userMessage = cleanedText,
                    conversationHistory = historyGroqMessages,
                    isJustListen = isJustListen,
                    category = selectedCategory
                )

                val aiMessage = Message(
                    text = responseContent,
                    isUser = false
                )

                _messages.add(aiMessage)

                wellbeingRepository.saveChatMessage(
                    ChatMessage(
                        text = responseContent,
                        isUser = false,
                        timestamp = aiMessage.timestamp
                    )
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                val errorText = e.localizedMessage ?: "AI Configuration Error."
                Log.e(TAG, "IllegalStateException during chat completion: $errorText")
                errorMessage = errorText
                _messages.add(Message(text = "⚠️ $errorText", isUser = false))
            } catch (e: IOException) {
                val errorText = e.localizedMessage ?: "Network or Groq API connection error."
                Log.e(TAG, "IOException during chat completion: $errorText")
                errorMessage = errorText
                _messages.add(Message(text = "⚠️ $errorText", isUser = false))
            } catch (e: Exception) {
                val errorText = e.localizedMessage ?: "An error occurred while connecting to Groq AI."
                Log.e(TAG, "Exception during chat completion: $errorText", e)
                errorMessage = errorText
                _messages.add(Message(text = "⚠️ $errorText", isUser = false))
            } finally {
                isTyping = false
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                wellbeingRepository.clearChatHistory()
                _messages.clear()
                _messages.add(
                    Message(
                        text = "Namaste! Main Saathi hoon — Your Safe Friend 🌱. Chat history clear ho gayi hai. Aaj man kaisa hai?",
                        isUser = false
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear chat history", e)
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}
