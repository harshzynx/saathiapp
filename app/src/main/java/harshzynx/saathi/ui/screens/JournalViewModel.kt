package harshzynx.saathi.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.JournalEntry
import harshzynx.saathi.data.repository.WellbeingRepository
import harshzynx.saathi.domain.GroqService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val wellbeingRepository: WellbeingRepository,
    private val groqService: GroqService
) : ViewModel() {

    private val _journals = mutableStateOf<List<JournalEntry>>(emptyList())
    val journals: State<List<JournalEntry>> = _journals

    var searchQuery = mutableStateOf("")
    var sortDescending = mutableStateOf(true)
    var currentContent = mutableStateOf("")
    var selectedMoodTag = mutableStateOf("😊 Peaceful")

    init {
        observeJournals()
        syncCloudJournals()
    }

    private fun observeJournals() {
        viewModelScope.launch {
            wellbeingRepository.getLocalJournalEntries().collectLatest { list ->
                _journals.value = list
            }
        }
    }

    private fun syncCloudJournals() {
        viewModelScope.launch {
            wellbeingRepository.syncJournalsFromCloud()
        }
    }

    fun getFilteredJournals(): List<JournalEntry> {
        val query = searchQuery.value.trim().lowercase()
        val filtered = if (query.isBlank()) {
            _journals.value
        } else {
            _journals.value.filter { 
                it.content.lowercase().contains(query) || (it.aiReflection?.lowercase()?.contains(query) == true)
            }
        }
        return if (sortDescending.value) {
            filtered.sortedByDescending { it.timestamp }
        } else {
            filtered.sortedBy { it.timestamp }
        }
    }

    fun saveJournal(onComplete: () -> Unit) {
        val textToSave = currentContent.value.trim()
        if (textToSave.isBlank()) return

        val taggedContent = "[${selectedMoodTag.value}] $textToSave"
        currentContent.value = ""
        onComplete()

        viewModelScope.launch {
            // Save entry immediately into Room & Firestore (Zero lag!)
            val insertedId = wellbeingRepository.saveJournalEntry(taggedContent, null)

            if (insertedId > 0) {
                try {
                    // Fetch AI reflection in background and update saved journal
                    val reflection = groqService.getChatResponse("The student wrote in their private journal: '$textToSave'. Give a 1-sentence kind, supportive reflection.")
                    if (reflection.isNotBlank()) {
                        wellbeingRepository.updateJournalReflection(insertedId, reflection)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteJournal(id: String) {
        viewModelScope.launch {
            wellbeingRepository.deleteJournalEntry(id)
        }
    }
}
