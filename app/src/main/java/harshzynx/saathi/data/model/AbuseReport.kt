package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class AbuseReport(
    val id: String = "",
    val reporterUid: String = "",
    val reporterEmail: String = "",
    val reportedUid: String = "",
    val reportedEmail: String = "",
    val reason: String = "",
    val status: String = "PENDING", // PENDING, INVESTIGATED, DISMISSED
    val timestamp: Timestamp = Timestamp.now()
)
