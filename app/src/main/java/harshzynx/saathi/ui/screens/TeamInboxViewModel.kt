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
class TeamInboxViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _tickets = mutableStateListOf<SupportTicket>()
    val tickets: List<SupportTicket> = _tickets

    private val _selectedTicketMessages = mutableStateListOf<SupportMessage>()
    val selectedTicketMessages: List<SupportMessage> = _selectedTicketMessages

    var selectedTicketId by mutableStateOf<String?>(null)
    var teamReplyInput by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    init {
        listenToAllTickets()
    }

    private fun listenToAllTickets() {
        isLoading = true
        firestore.collection("support_tickets")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                isLoading = false
                if (snapshot != null) {
                    val list = snapshot.toObjects(SupportTicket::class.java)
                    _tickets.clear()
                    _tickets.addAll(list)
                }
            }
    }

    fun selectTicket(ticketId: String) {
        selectedTicketId = ticketId
        listenToTicketMessages(ticketId)
    }

    private fun listenToTicketMessages(ticketId: String) {
        firestore.collection("support_tickets").document(ticketId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(SupportMessage::class.java)
                    _selectedTicketMessages.clear()
                    _selectedTicketMessages.addAll(list)
                }
            }
    }

    fun sendTeamReply() {
        val text = teamReplyInput.trim()
        val tId = selectedTicketId
        if (text.isBlank() || tId == null) return

        val counselorName = auth.currentUser?.displayName ?: "Saathi Counselor"
        val msgRef = firestore.collection("support_tickets").document(tId).collection("messages").document()
        val msg = SupportMessage(
            id = msgRef.id,
            senderId = auth.currentUser?.uid ?: "counselor_1",
            senderName = counselorName,
            text = text,
            timestamp = Timestamp.now()
        )

        teamReplyInput = ""
        viewModelScope.launch {
            try {
                msgRef.set(msg).await()
                firestore.collection("support_tickets").document(tId).update("status", "active").await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
