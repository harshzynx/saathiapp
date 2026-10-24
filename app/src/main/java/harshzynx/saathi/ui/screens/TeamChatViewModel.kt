package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.SupportMessage
import harshzynx.saathi.data.model.SupportTicket
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TeamChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _messages = mutableStateListOf<SupportMessage>()
    val messages: List<SupportMessage> = _messages

    var ticketId by mutableStateOf<String?>(null)
    var inputMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun initializeChat(memberName: String) {
        val uid = auth.currentUser?.uid ?: "guest_user"
        val userName = auth.currentUser?.displayName ?: "Student"
        val cleanStaff = memberName.replace(" ", "_").lowercase()
        val deterministicTicketId = "ticket_${uid}_$cleanStaff"

        ticketId = deterministicTicketId
        isLoading = true

        viewModelScope.launch {
            try {
                val docRef = firestore.collection("support_tickets").document(deterministicTicketId)
                val snapshot = docRef.get().await()

                if (!snapshot.exists()) {
                    val newTicket = SupportTicket(
                        id = deterministicTicketId,
                        userId = uid,
                        userName = userName,
                        assignedStaff = memberName,
                        status = "active",
                        createdAt = Timestamp.now()
                    )
                    docRef.set(newTicket).await()
                }

                listenToMessages(deterministicTicketId, memberName)
            } catch (e: Exception) {
                e.printStackTrace()
                listenToMessages(deterministicTicketId, memberName)
            } finally {
                isLoading = false
            }
        }
    }

    private fun listenToMessages(tId: String, memberName: String) {
        firestore.collection("support_tickets").document(tId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(SupportMessage::class.java)
                    _messages.clear()
                    _messages.addAll(list)

                    if (_messages.isEmpty()) {
                        // Default welcome message from staff
                        _messages.add(
                            SupportMessage(
                                id = "welcome_1",
                                senderId = "staff",
                                senderName = memberName,
                                text = "Namaste! Main $memberName hoon Saathi Support Team se. Aap kaise hain aaj? Koyi bhi baat share kar sakte hain 🫂",
                                timestamp = Timestamp.now()
                            )
                        )
                    }
                }
            }
    }

    fun sendMessage(memberName: String) {
        val text = inputMessage.trim()
        if (text.isBlank()) return

        val uid = auth.currentUser?.uid ?: "guest_user"
        val userName = auth.currentUser?.displayName ?: "Student"
        val cleanStaff = memberName.replace(" ", "_").lowercase()
        val tId = ticketId ?: "ticket_${uid}_$cleanStaff"
        ticketId = tId

        val msgRef = firestore.collection("support_tickets").document(tId).collection("messages").document()
        val msg = SupportMessage(
            id = msgRef.id,
            senderId = uid,
            senderName = userName,
            text = text,
            timestamp = Timestamp.now()
        )

        // Optimistic UI update so message appears on screen immediately
        if (_messages.none { it.id == msg.id }) {
            _messages.add(msg)
        }

        inputMessage = ""

        viewModelScope.launch {
            try {
                // Ensure parent ticket document exists
                val ticketRef = firestore.collection("support_tickets").document(tId)
                ticketRef.set(
                    SupportTicket(
                        id = tId,
                        userId = uid,
                        userName = userName,
                        assignedStaff = memberName,
                        status = "active",
                        createdAt = Timestamp.now()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()

                // Save message in subcollection
                msgRef.set(msg).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
