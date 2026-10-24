package harshzynx.saathi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import harshzynx.saathi.data.local.ApiKeyManager
import harshzynx.saathi.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val apiKeyManager: ApiKeyManager
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    private val usersCollection
        get() = firestore.collection("users")

    private fun defaultName(user: FirebaseUser): String {
        return user.displayName
            ?.takeIf { it.isNotBlank() }
            ?: user.email
                ?.substringBefore("@")
                ?.takeIf { it.isNotBlank() }
                ?.replaceFirstChar { it.uppercase() }
            ?: "Saathi Guest"
    }

    private suspend fun ensureUserProfile(firebaseUser: FirebaseUser) {
        val uid = firebaseUser.uid
        try {
            val document = usersCollection.document(uid).get().await()

            if (!document.exists()) {
                val profile = mapOf(
                    "uid" to uid,
                    "name" to defaultName(firebaseUser),
                    "email" to (firebaseUser.email ?: ""),
                    "educationLevel" to "College",
                    "role" to "USER",
                    "isStaffActive" to false
                )

                usersCollection.document(uid).set(profile).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getUserProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null

        ensureUserProfile(firebaseUser)

        return try {
            val document = usersCollection
                .document(firebaseUser.uid)
                .get()
                .await()

            document.toObject(User::class.java) ?: User(
                uid = firebaseUser.uid,
                name = defaultName(firebaseUser),
                email = firebaseUser.email ?: ""
            )
        } catch (e: Exception) {
            User(
                uid = firebaseUser.uid,
                name = defaultName(firebaseUser),
                email = firebaseUser.email ?: ""
            )
        }
    }

    suspend fun saveUserProfile(user: User) {
        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException("Please sign in before saving your profile.")

        val uid = firebaseUser.uid
        val documentRef = usersCollection.document(uid)

        val profileUpdates = mapOf(
            "name" to user.name.trim(),
            "email" to (firebaseUser.email ?: ""),
            "educationLevel" to user.educationLevel,
            "institutionName" to user.institutionName,
            "collegeEmail" to user.collegeEmail,
            "studentMobile" to user.studentMobile,
            "parentMobile" to user.parentMobile,
            "teacherEmail" to user.teacherEmail,
            "friendMobile" to user.friendMobile,
            "trustedPersonName" to user.trustedPersonName,
            "trustedPersonEmail" to user.trustedPersonEmail,
            "safetyAlertConsent" to user.safetyAlertConsent,
            "isOnboardingCompleted" to user.isOnboardingCompleted
        )

        try {
            val snapshot = documentRef.get().await()
            if (!snapshot.exists()) {
                val fullMap = profileUpdates.toMutableMap()
                fullMap["uid"] = uid
                fullMap["role"] = "USER"
                fullMap["isStaffActive"] = false
                documentRef.set(fullMap).await()
            } else {
                documentRef.update(profileUpdates).await()
            }
        } catch (e: Exception) {
            documentRef.set(profileUpdates, com.google.firebase.firestore.SetOptions.merge()).await()
        }
    }

    suspend fun signIn(email: String, pass: String) {
        require(email.isNotBlank()) { "Email is required." }
        require(pass.isNotEmpty()) { "Password is required." }

        auth.signInWithEmailAndPassword(email.trim(), pass).await()

        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException("Login failed. Please try again.")

        ensureUserProfile(firebaseUser)
        apiKeyManager.setLoggedIn(true)
    }

    suspend fun signUp(email: String, pass: String, name: String) {
        require(email.isNotBlank()) { "Email is required." }
        require(pass.length >= 6) {
            "Password must contain at least 6 characters."
        }
        require(name.isNotBlank()) { "Name is required." }

        val result = auth.createUserWithEmailAndPassword(
            email.trim(),
            pass
        ).await()

        val firebaseUser = result.user
            ?: throw IllegalStateException("Account creation failed.")

        val profile = mapOf(
            "uid" to firebaseUser.uid,
            "name" to name.trim(),
            "email" to (firebaseUser.email ?: email.trim()),
            "educationLevel" to "College",
            "role" to "USER",
            "isStaffActive" to false
        )

        try {
            usersCollection.document(firebaseUser.uid)
                .set(profile)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        apiKeyManager.setLoggedIn(true)
    }

    suspend fun signInWithGoogleCredential(idToken: String) {
        require(idToken.isNotBlank()) { "Google sign-in token is missing." }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()

        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException("Google sign-in failed.")

        ensureUserProfile(firebaseUser)
        apiKeyManager.setLoggedIn(true)
    }

    suspend fun sendPasswordResetEmail(email: String) {
        require(email.isNotBlank()) { "Email is required." }
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    suspend fun signInAnonymously() {
        try {
            auth.signInAnonymously().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            ensureUserProfile(firebaseUser)
        }
        
        apiKeyManager.setLoggedIn(true)
    }

    suspend fun deleteAccountAndUserData() {
        val firebaseUser = auth.currentUser ?: return

        try {
            usersCollection.document(firebaseUser.uid).delete().await()
            firebaseUser.delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        apiKeyManager.setLoggedIn(false)
    }

    fun signOut() {
        auth.signOut()
        apiKeyManager.setLoggedIn(false)
    }
}
