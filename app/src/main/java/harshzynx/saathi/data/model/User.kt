package harshzynx.saathi.data.model

import com.google.firebase.Timestamp

enum class UserRole {
    USER,
    STAFF,
    OWNER
}

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val educationLevel: String = "",
    val institutionName: String? = null,
    val collegeEmail: String? = null,
    val studentMobile: String? = null,
    val parentMobile: String? = null,
    val teacherEmail: String? = null,
    val friendMobile: String? = null,
    val linkedCounselorEmail: String? = null,
    val trustedPersonName: String? = null,
    val trustedPersonEmail: String? = null,
    val isGuardianLinked: Boolean = false,
    val safetyAlertConsent: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
    val guardianLastAlertSent: Timestamp? = null,
    val consecutiveLowMoodCount: Int = 0,
    val lastActiveTimestamp: Timestamp? = null,

    val isCounsellor: Boolean = false,
    val role: String = UserRole.USER.name,
    val isStaffActive: Boolean = false,

    // Account Status & Governance
    val accountStatus: String = "ACTIVE", // ACTIVE, SUSPENDED, FLAGGED
    val deletionRequested: Boolean = false,
    val flagReason: String? = null
)
