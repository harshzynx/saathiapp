package harshzynx.saathi.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.SupportMessage
import harshzynx.saathi.data.model.SupportTicket
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TeamSupportViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _messages = mutableStateListOf<SupportMessage>()
    val messages: List<SupportMessage> = _messages

    var currentTicketId = mutableStateOf<String?>(null)
    var inputMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    init {
        initializeTicketAndListen()
    }

    private fun initializeTicketAndListen() {
        val uid = auth.currentUser?.uid ?: "guest_user"
        isLoading.value = true
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("support_tickets")
                    .whereEqualTo("userId", uid)
                    .whereNotEqualTo("status", "closed")
                    .get()
                    .await()

                val ticketId = if (!querySnapshot.isEmpty) {
                    querySnapshot.documents[0].id
                } else {
                    val docRef = firestore.collection("support_tickets").document()
                    val newTicket = SupportTicket(
                        id = docRef.id,
                        userId = uid,
                        userName = auth.currentUser?.displayName ?: "Student"
                    )
                    docRef.set(newTicket).await()
                    docRef.id
                }

                currentTicketId.value = ticketId
                listenToMessages(ticketId)
            } catch (e: Exception) {
                currentTicketId.value = "demo_ticket"
                _messages.add(SupportMessage("1", "team", "Saathi Support Desk", "Namaste! Hamari support team aapki madad ke liye yahan hai. Aap apni baat yahan share kar sakte hain 🫂", com.google.firebase.Timestamp.now()))
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun listenToMessages(ticketId: String) {
        if (ticketId == "demo_ticket") return

        firestore.collection("support_tickets").document(ticketId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(SupportMessage::class.java)
                    _messages.clear()
                    _messages.addAll(list)
                    if (_messages.isEmpty()) {
                        _messages.add(SupportMessage("1", "team", "Saathi Support Desk", "Namaste! Support team connected. How can we help you today?", com.google.firebase.Timestamp.now()))
                    }
                }
            }
    }

    fun sendMessage() {
        val text = inputMessage.value.trim()
        val tId = currentTicketId.value
        if (text.isBlank() || tId == null) return

        val uid = auth.currentUser?.uid ?: "guest_user"
        val msgRef = firestore.collection("support_tickets").document(tId).collection("messages").document()
        val msg = SupportMessage(
            id = msgRef.id,
            senderId = uid,
            senderName = "Student",
            text = text,
            timestamp = com.google.firebase.Timestamp.now()
        )

        inputMessage.value = ""
        viewModelScope.launch {
            try {
                if (tId != "demo_ticket") {
                    msgRef.set(msg).await()
                } else {
                    _messages.add(msg)
                    delay(1000)
                    _messages.add(SupportMessage(
                        id = "reply_${System.currentTimeMillis()}",
                        senderId = "team",
                        senderName = "Saathi Support Desk",
                        text = "Aapki baat sun kar hamein dukh hua. Hum jald hi aapse phone par connect karenge. Himmat rakhiye! ❤️",
                        timestamp = com.google.firebase.Timestamp.now()
                    ))
                }
            } catch (e: Exception) {
                _messages.add(msg)
            }
        }
    }
}
