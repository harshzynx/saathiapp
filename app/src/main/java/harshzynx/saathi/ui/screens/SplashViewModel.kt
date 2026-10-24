package harshzynx.saathi.ui.screens

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.local.ApiKeyManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    suspend fun checkAuthStatus(): String {
        val user = auth.currentUser
        if (user != null || apiKeyManager.isLoggedIn()) {
            val email = user?.email?.trim()?.lowercase() ?: ""
            if (email == "harshsharma18089@gmail.com") {
                return "admin_panel"
            }
            if (email.isNotBlank()) {
                try {
                    val staffSnapshot = firestore.collection("team_staff")
                        .whereEqualTo("email", email)
                        .get()
                        .await()
                    if (!staffSnapshot.isEmpty) {
                        return "team_inbox"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return "dashboard"
        }
        return "auth"
    }
}
