package harshzynx.saathi.data.model

data class TeamMember(
    val id: String = "",
    val name: String = "",
    val gender: String = "Female", // Female, Male
    val role: String = "Team Member",
    val language: String = "Hindi / English",
    val isAvailable: Boolean = true,
    val bio: String = "",
    val tags: List<String> = emptyList(),
    val avatarUrl: String = ""
)

data class SupportRoom(
    val id: String = "",
    val title: String = "",
    val activeCount: Int = 0,
    val category: String = ""
)

data class TrustedContact(
    val id: String = "",
    val name: String = "",
    val relation: String = "Parent",
    val phone: String = ""
)
