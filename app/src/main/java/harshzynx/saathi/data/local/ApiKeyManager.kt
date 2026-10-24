package harshzynx.saathi.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ApiKeyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences(
            "saathi_secure_prefs",
            Context.MODE_PRIVATE
        )
    }

    open fun getApiKey(): String {
        val customKey = prefs.getString("custom_groq_api_key", null)
            ?.trim()
            .orEmpty()

        if (customKey.isNotBlank()) {
            return if (customKey.startsWith("Bearer ", ignoreCase = true)) {
                customKey
            } else {
                "Bearer $customKey"
            }
        }

        return ""
    }

    open fun saveApiKey(key: String) {
        val cleanedKey = key.trim().removePrefix("Bearer ").trim()

        prefs.edit()
            .remove("custom_groq_api_key")
            .apply()

        if (cleanedKey.isNotBlank()) {
            prefs.edit()
                .putString("custom_groq_api_key", cleanedKey)
                .apply()
        }
    }

    open fun getWebClientId(): String {
        return prefs.getString(
            "google_web_client_id",
            "491621676558-ja48d2748sfoig9f6s3bkt7ketp0r0ke.apps.googleusercontent.com"
        )?.trim().orEmpty()
    }

    open fun saveWebClientId(clientId: String) {
        if (clientId.isNotBlank()) {
            prefs.edit().putString("google_web_client_id", clientId.trim()).apply()
        }
    }

    open fun isComplianceAccepted(): Boolean {
        return prefs.getBoolean("compliance_accepted", false)
    }

    open fun setComplianceAccepted(accepted: Boolean) {
        prefs.edit().putBoolean("compliance_accepted", accepted).apply()
    }

    open fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    open fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit()
            .putBoolean("is_logged_in", loggedIn)
            .apply()
    }

    open fun clearApiKey() {
        prefs.edit()
            .remove("custom_groq_api_key")
            .apply()
    }
}
