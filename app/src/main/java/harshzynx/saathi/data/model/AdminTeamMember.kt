package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class AdminTeamMember(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Counselor", // Senior Counselor, Peer Volunteer, Staff Supervisor
    val genderCategory: String = "All", // All, Male, Female, Non-Binary
    val status: String = "ACTIVE", // ACTIVE, INACTIVE
    val availability: String = "AVAILABLE", // AVAILABLE, ON_BREAK, OFFLINE
    val supportTimings: String = "9:00 AM - 6:00 PM",
    val activeTicketsCount: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
