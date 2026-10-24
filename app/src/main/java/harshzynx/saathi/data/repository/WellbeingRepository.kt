package harshzynx.saathi.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import harshzynx.saathi.data.local.JournalDao
import harshzynx.saathi.data.local.LocalJournalEntry
import harshzynx.saathi.data.model.ChatMessage
import harshzynx.saathi.data.model.Habit
import harshzynx.saathi.data.model.JournalEntry
import harshzynx.saathi.data.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WellbeingRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val journalDao: JournalDao,
) {
    private val userId: String? get() = auth.currentUser?.uid
    private val cachedChatHistory = mutableListOf<ChatMessage>()

    // Moods (Cloud Sync)
    suspend fun saveMoodEntry(entry: MoodEntry) {
        val uid = userId ?: return
        try {
            val docRef = firestore.collection("users").document(uid).collection("moods").document()
            val entryWithId = entry.copy(id = docRef.id, userId = uid)
            docRef.set(entryWithId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getMoodEntries(): List<MoodEntry> {
        val uid = userId ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users").document(uid).collection("moods")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(MoodEntry::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Journals (Local First with Async Cloud Sync)
    suspend fun saveJournalEntry(content: String, reflection: String?): Long {
        return try {
            val entry = LocalJournalEntry(content = content, aiReflection = reflection)
            val insertedId = journalDao.insert(entry)
            
            val uid = userId
            if (uid != null) {
                val cloudEntry = JournalEntry(id = insertedId.toString(), content = content, aiReflection = reflection)
                firestore.collection("users").document(uid).collection("journals").document(insertedId.toString()).set(cloudEntry)
            }
            insertedId
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    suspend fun updateJournalReflection(id: Long, reflection: String) {
        try {
            journalDao.updateReflection(id, reflection)
            val uid = userId
            if (uid != null) {
                firestore.collection("users").document(uid).collection("journals").document(id.toString())
                    .update("aiReflection", reflection)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun syncJournalsFromCloud() {
        val uid = userId ?: return
        try {
            val snapshot = firestore.collection("users").document(uid).collection("journals").get().await()
            val cloudEntries = snapshot.toObjects(JournalEntry::class.java)
            cloudEntries.forEach { cloud ->
                val idLong = cloud.id.toLongOrNull() ?: 0L
                val local = LocalJournalEntry(id = idLong, content = cloud.content, aiReflection = cloud.aiReflection)
                journalDao.insert(local)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteJournalEntry(id: String) {
        try {
            val localId = id.toLongOrNull()
            if (localId != null) {
                journalDao.deleteById(localId)
            }
            val uid = userId
            if (uid != null) {
                firestore.collection("users").document(uid).collection("journals").document(id).delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLocalJournalEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllEntries().map { locals ->
            locals.map { 
                JournalEntry(
                    id = it.id.toString(),
                    content = it.content,
                    aiReflection = it.aiReflection
                )
            }
        }
    }

    // Chat Persistence (Local Memory + Async Non-blocking Cloud Sync)
    suspend fun saveChatMessage(message: ChatMessage) {
        cachedChatHistory.add(message)
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).collection("chats").add(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getChatHistory(): List<ChatMessage> {
        val uid = userId ?: return cachedChatHistory.toList()
        return try {
            val snapshot = firestore.collection("users").document(uid).collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
            val cloudList = snapshot.toObjects(ChatMessage::class.java)
            if (cloudList.isNotEmpty()) {
                cachedChatHistory.clear()
                cachedChatHistory.addAll(cloudList)
                cloudList
            } else {
                cachedChatHistory.toList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cachedChatHistory.toList()
        }
    }

    suspend fun clearChatHistory() {
        cachedChatHistory.clear()
        val uid = userId ?: return
        try {
            val snapshot = firestore.collection("users").document(uid).collection("chats").get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Habits Persistence
    suspend fun saveHabits(habits: List<Habit>) {
        val uid = userId ?: return
        try {
            firestore.collection("users").document(uid).update("habits", habits)
        } catch (e: Exception) {
            try {
                firestore.collection("users").document(uid).set(mapOf("habits" to habits))
            } catch (inner: Exception) {
                inner.printStackTrace()
            }
        }
    }

    suspend fun getHabits(): List<Habit> {
        val uid = userId ?: return emptyList()
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val list = doc.get("habits") as? List<Map<String, Any>>
            list?.map { 
                Habit(
                    id = (it["id"] as? Long)?.toInt() ?: 0,
                    name = it["name"] as? String ?: "",
                    isCompleted = it["isCompleted"] as? Boolean ?: false
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
