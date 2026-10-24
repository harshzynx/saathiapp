package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.local.ApiKeyManager
import harshzynx.saathi.data.model.User
import harshzynx.saathi.data.repository.AuthRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var name by mutableStateOf("")
    var isSignUp by mutableStateOf(false)
    var isPasswordVisible by mutableStateOf(false)
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val isUserLoggedIn: Boolean get() = authRepository.currentUser != null

    fun getWebClientId(): String = apiKeyManager.getWebClientId()

    fun saveWebClientId(id: String) {
        apiKeyManager.saveWebClientId(id)
    }

    fun toggleAuthMode() {
        isSignUp = !isSignUp
        errorMessage = null
        password = ""
        confirmPassword = ""
    }

    fun authenticate(onSuccess: () -> Unit) {
        errorMessage = null
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val cleanName = name.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            errorMessage = "Email and password cannot be empty."
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorMessage = "Please enter a valid email address."
            return
        }

        if (cleanPassword.length < 6) {
            errorMessage = "Password must be at least 6 characters long."
            return
        }

        if (isSignUp) {
            if (cleanName.isBlank()) {
                errorMessage = "Please enter your full name."
                return
            }
            if (cleanPassword != confirmPassword.trim()) {
                errorMessage = "Passwords do not match."
                return
            }
        }

        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            try {
                if (isSignUp) {
                    authRepository.signUp(cleanEmail, cleanPassword, cleanName)
                } else {
                    authRepository.signIn(cleanEmail, cleanPassword)
                }
                isLoading = false
                onSuccess()
            } catch (e: FirebaseAuthInvalidUserException) {
                isLoading = false
                errorMessage = "No account found with this email. Please Sign Up first."
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                isLoading = false
                errorMessage = "Incorrect password or invalid credentials. Please try again."
            } catch (e: FirebaseAuthUserCollisionException) {
                isLoading = false
                errorMessage = "An account already exists with this email address. Please Log In."
            } catch (e: FirebaseAuthWeakPasswordException) {
                isLoading = false
                errorMessage = "Password is too weak. Please use a stronger password."
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Authentication failed. Please check your connection and try again."
            }
        }
    }

    fun continueAsGuest(onSuccess: () -> Unit) {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                authRepository.signInAnonymously()
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Guest sign-in failed: ${e.localizedMessage}"
            }
        }
    }

    fun signInWithGoogleToken(idToken: String, onSuccess: () -> Unit) {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                authRepository.signInWithGoogleCredential(idToken)
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                errorMessage = "Google sign-in failed: ${e.localizedMessage}"
            }
        }
    }

    fun signInWithGoogleAccount(googleEmail: String, googleName: String, onSuccess: () -> Unit) {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                authRepository.signInAnonymously()
                val current = authRepository.getUserProfile() ?: User(uid = authRepository.currentUser?.uid ?: "user")
                val updated = current.copy(
                    name = googleName.ifBlank { "Google Friend" },
                    email = googleEmail
                )
                authRepository.saveUserProfile(updated)
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                onSuccess()
            }
        }
    }

    fun resetPassword(resetEmail: String, onResult: (Boolean, String) -> Unit) {
        val cleanEmail = resetEmail.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onResult(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            try {
                authRepository.sendPasswordResetEmail(cleanEmail)
                onResult(true, "Password reset email sent! Check your inbox.")
            } catch (e: Exception) {
                onResult(false, e.localizedMessage ?: "Failed to send reset email. Verify your email address.")
            }
        }
    }
}
