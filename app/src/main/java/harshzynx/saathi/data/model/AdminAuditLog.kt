package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class AdminAuditLog(
    val id: String = "",
    val actorEmail: String = "",
    val action: String = "",
    val details: String = "",
    val affectedRecordId: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
