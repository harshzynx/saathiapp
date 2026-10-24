package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class CommunityPost(
    val id: String = "",
    val content: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val likesCount: Int = 0
)
