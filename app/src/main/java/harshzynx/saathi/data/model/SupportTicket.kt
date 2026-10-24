package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val userName: String = "Student",
    val assignedStaff: String = "",
    val assignedStaffEmail: String = "",
    val category: String = "General Support",
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val lastMessageText: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

data class SupportMessage(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
