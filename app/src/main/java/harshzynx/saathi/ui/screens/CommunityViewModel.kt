package harshzynx.saathi.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.CommunityPost
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _posts = mutableStateOf<List<CommunityPost>>(emptyList())
    val posts: State<List<CommunityPost>> = _posts

    var newPostContent = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("community_posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .await()
                _posts.value = snapshot.toObjects(CommunityPost::class.java)
            } catch (e: Exception) {
                // Fallback to default posts if collection is empty or offline
                _posts.value = listOf(
                    CommunityPost("1", "Exams are tough, but I realized that my health is more important. Taking it one day at a time."),
                    CommunityPost("2", "The 4-4-4 breathing really helped me during my panic attack last night. Grateful for this space."),
                    CommunityPost("3", "It's okay to not be okay. Today was a heavy day, but I'm still here."),
                    CommunityPost("4", "College life can be lonely sometimes, but talking to Saathi AI makes me feel heard.")
                )
            }
        }
    }

    fun addPost(onSuccess: () -> Unit, onUnsafe: () -> Unit) {
        val content = newPostContent.value.trim()
        if (content.isBlank()) return

        // Basic AI Safety Moderation Filter
        val lower = content.lowercase()
        if (lower.contains("suicide") || lower.contains("marna") || lower.contains("khatam") || lower.contains("marne")) {
            onUnsafe()
            return
        }

        viewModelScope.launch {
            try {
                val docRef = firestore.collection("community_posts").document()
                val post = CommunityPost(id = docRef.id, content = content)
                docRef.set(post).await()
                newPostContent.value = ""
                fetchPosts()
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = "Failed to post. Please check internet."
            }
        }
    }
}
