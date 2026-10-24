package harshzynx.saathi.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class AnimeAvatar(
    val id: String,
    val name: String,
    val title: String,
    val emoji: String,
    val auraColorHex: Long,
    val quote: String
)

@Singleton
class AnimeAvatarManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("anime_avatar_prefs", Context.MODE_PRIVATE)

    val availableAvatars = listOf(
        AnimeAvatar("kira", "Kira", "Calm Samurai ⚔️", "🥷", 0xFF00796BL, "Daijoubu! Every storm passes. Stay focused! ⚔️"),
        AnimeAvatar("aria", "Aria", "Gentle Light Mage ✨", "🧝‍♀️", 0xFF7E57C2L, "Believe in your inner magic! You are not alone ✨"),
        AnimeAvatar("yuki", "Yuki", "Cheerful Scholar 📚", "🧑‍🎓", 0xFF0288D1L, "One step at a time! We will ace this together 📚"),
        AnimeAvatar("ren", "Ren", "Loyal Guardian Shield 🛡️", "🧙‍♂️", 0xFFE65100L, "I stand by your side. Nothing can break your spirit 🛡️")
    )

    fun getSelectedAvatarId(): String {
        return prefs.getString("selected_avatar_id", "kira") ?: "kira"
    }

    fun saveSelectedAvatarId(id: String) {
        prefs.edit().putString("selected_avatar_id", id).apply()
    }

    fun getSelectedAvatar(): AnimeAvatar {
        val id = getSelectedAvatarId()
        return availableAvatars.find { it.id == id } ?: availableAvatars[0]
    }
}
