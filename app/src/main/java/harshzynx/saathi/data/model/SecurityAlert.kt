package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

data class SecurityAlert(
    val id: String = "",
    val userEmail: String = "",
    val alertType: String = "SUSPICIOUS_LOGIN", // SUSPICIOUS_LOGIN, ROLE_TAMPER_ATTEMPT, MULTIPLE_FAILED_AUTH
    val severity: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val details: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
