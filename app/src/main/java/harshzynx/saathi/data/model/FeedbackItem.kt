package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class FeedbackItem(
    val id: String = "",
    val userUid: String = "",
    val userEmail: String = "",
    val category: String = "Feature Request", // Bug Report, Feature Request, Suggestion
    val message: String = "",
    val status: String = "OPEN", // OPEN, IN_REVIEW, RESOLVED
    val timestamp: Timestamp = Timestamp.now()
)
