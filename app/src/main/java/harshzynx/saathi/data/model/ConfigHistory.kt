package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class ConfigHistory(
    val id: String = "",
    val version: Int = 1,
    val previousConfigSummary: String = "",
    val newConfigSummary: String = "",
    val changedBy: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
