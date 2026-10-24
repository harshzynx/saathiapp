package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class MoodEntry(
    val id: String = "",
    val userId: String = "",
    val moodValue: Int = 3, // 1 (Very Low) to 5 (Very Happy)
    val stressLevel: Int = 1, // 1 (Low) to 5 (Extreme)
    val sleepHours: Float = 8f,
    val studyPressure: Int = 1,
    val energyLevel: Int = 3,
    val timestamp: Timestamp = Timestamp.now(),
    val note: String? = null
)
