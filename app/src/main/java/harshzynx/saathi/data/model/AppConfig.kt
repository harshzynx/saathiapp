package harshzynx.saathi.data.model

data class AppConfig(
    val aiEnabled: Boolean = true,
    val selectedAiModel: String = "llama-3.3-70b-versatile",
    val humanSupportEnabled: Boolean = true,
    val maintenanceMode: Boolean = false,
    val communityEnabled: Boolean = true,
    val supportOperatingHours: String = "9:00 AM - 9:00 PM IST",
    val supportContactEmail: String = "support@saathi.app",
    val privacyPolicyUrl: String = "https://saathi.app/privacy",
    val termsOfServiceUrl: String = "https://saathi.app/terms",
    val playStoreUrl: String = "https://play.google.com/store/apps/details?id=harshzynx.saathi",
    val latestVersionCode: Int = 1,
    val latestVersionName: String = "1.0.0",
    val minSupportedVersionCode: Int = 1,
    val releaseNotes: String = "Performance improvements and new Mind Games!",
    val updateAnnouncementEnabled: Boolean = false
)
