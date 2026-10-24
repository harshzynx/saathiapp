package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.local.ApiKeyManager
import harshzynx.saathi.data.model.User
import harshzynx.saathi.data.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var educationLevel by mutableStateOf("College")
    var institutionName by mutableStateOf("")
    var collegeEmail by mutableStateOf("")
    var studentMobile by mutableStateOf("")
    var parentMobile by mutableStateOf("")
    var teacherEmail by mutableStateOf("")
    var friendMobile by mutableStateOf("")
    
    var groqApiKey by mutableStateOf("")
    var saveMessage by mutableStateOf<String?>(null)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            email = authRepository.currentUser?.email ?: ""
            groqApiKey = apiKeyManager.getApiKey().removePrefix("Bearer ")
            authRepository.getUserProfile()?.let { user ->
                name = user.name.ifBlank { authRepository.currentUser?.displayName ?: "Saathi Friend" }
                if (user.email.isNotBlank()) email = user.email
                educationLevel = user.educationLevel.ifBlank { "College" }
                institutionName = user.institutionName ?: ""
                collegeEmail = user.collegeEmail ?: ""
                studentMobile = user.studentMobile ?: ""
                parentMobile = user.parentMobile ?: ""
                teacherEmail = user.teacherEmail ?: ""
                friendMobile = user.friendMobile ?: ""
            }
        }
    }

    fun saveProfile(onComplete: () -> Unit) {
        saveMessage = null
        viewModelScope.launch {
            val current = authRepository.getUserProfile() ?: User(uid = authRepository.currentUser?.uid ?: "user")
            val updated = current.copy(
                name = name.ifBlank { "Saathi Friend" },
                email = email,
                educationLevel = educationLevel,
                institutionName = institutionName.ifBlank { null },
                collegeEmail = collegeEmail.ifBlank { null },
                studentMobile = studentMobile.ifBlank { null },
                parentMobile = parentMobile.ifBlank { null },
                teacherEmail = teacherEmail.ifBlank { null },
                friendMobile = friendMobile.ifBlank { null },
                isOnboardingCompleted = true
            )
            authRepository.saveUserProfile(updated)
            if (groqApiKey.isNotBlank()) {
                apiKeyManager.saveApiKey(groqApiKey)
            }
            saveMessage = "Profile saved successfully! ✨"
            onComplete()
        }
    }

    fun logout(onLogout: () -> Unit) {
        authRepository.signOut()
        onLogout()
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.deleteAccountAndUserData()
                onDeleted()
            } catch (e: Exception) {
                authRepository.signOut()
                onDeleted()
            }
        }
    }
}
