package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class JournalEntry(
    val id: String = "",
    val userId: String = "",
    val content: String = "",
    val aiReflection: String? = null,
    val timestamp: Timestamp = Timestamp.now()
)
