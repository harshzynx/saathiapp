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
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var institutionName by mutableStateOf("")
    var collegeEmail by mutableStateOf("")
    var studentMobile by mutableStateOf("")
    var parentMobile by mutableStateOf("")
    var teacherEmail by mutableStateOf("")
    var friendMobile by mutableStateOf("")
    var educationLevel by mutableStateOf("College")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun saveOnboardingData(onComplete: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val currentUid = authRepository.currentUser?.uid ?: "guest_user"
                val existing = authRepository.getUserProfile() ?: User(uid = currentUid)
                
                val updated = existing.copy(
                    institutionName = institutionName.ifBlank { null },
                    collegeEmail = collegeEmail.ifBlank { null },
                    studentMobile = studentMobile.ifBlank { null },
                    parentMobile = parentMobile.ifBlank { null },
                    teacherEmail = teacherEmail.ifBlank { null },
                    friendMobile = friendMobile.ifBlank { null },
                    educationLevel = educationLevel,
                    isOnboardingCompleted = true,
                    trustedPersonName = "Parent / Guardian",
                    trustedPersonEmail = teacherEmail.ifBlank { null },
                    isGuardianLinked = parentMobile.isNotBlank()
                )

                authRepository.saveUserProfile(updated)
                isLoading = false
                onComplete()
            } catch (e: Exception) {
                isLoading = false
                // Even on error, ensure onboarding is marked completed so user isn't stuck
                onComplete()
            }
        }
    }
}
