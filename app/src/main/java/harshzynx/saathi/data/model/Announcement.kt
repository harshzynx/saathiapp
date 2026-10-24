package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val targetGroup: String = "ALL", // ALL, STUDENTS, STAFF
    val authorEmail: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
