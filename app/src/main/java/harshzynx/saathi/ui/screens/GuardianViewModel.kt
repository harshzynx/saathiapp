package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.User
import harshzynx.saathi.data.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardianViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var guardianName by mutableStateOf("")
    var guardianEmail by mutableStateOf("")
    var isLinked by mutableStateOf(value = false)
    var safetyAlertConsent by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
    var showInvitePrompt by mutableStateOf(false)

    init {
        loadGuardianInfo()
    }

    private fun loadGuardianInfo() {
        viewModelScope.launch {
            authRepository.getUserProfile()?.let { user ->
                guardianName = user.trustedPersonName ?: ""
                guardianEmail = user.trustedPersonEmail ?: ""
                isLinked = user.isGuardianLinked
                safetyAlertConsent = user.safetyAlertConsent
            }
        }
    }

    fun updateGuardianLink(linked: Boolean, onComplete: () -> Unit) {
        val currentUid = authRepository.currentUser?.uid ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                val profile = authRepository.getUserProfile() ?: User(uid = currentUid)
                val updatedUser = profile.copy(
                    trustedPersonName = guardianName,
                    trustedPersonEmail = guardianEmail,
                    isGuardianLinked = linked,
                    safetyAlertConsent = safetyAlertConsent
                )
                authRepository.saveUserProfile(updatedUser)
                isLinked = linked
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
                onComplete()
            }
        }
    }
}
