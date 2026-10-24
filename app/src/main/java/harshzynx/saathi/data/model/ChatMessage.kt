package harshzynx.saathi.data.model

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val isUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
