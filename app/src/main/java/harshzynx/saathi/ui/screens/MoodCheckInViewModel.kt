package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.MoodEntry
import harshzynx.saathi.data.repository.WellbeingRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoodCheckInViewModel @Inject constructor(
    private val wellbeingRepository: WellbeingRepository
) : ViewModel() {

    var moodValue by mutableStateOf(3)
    var stressLevel by mutableStateOf(1)
    var sleepHours by mutableStateOf(8f)
    var studyPressure by mutableStateOf(1)
    var energyLevel by mutableStateOf(3)
    var selectedTrigger by mutableStateOf("📚 Exams / Studies")
    var note by mutableStateOf("")

    fun saveMoodEntry(onComplete: () -> Unit) {
        viewModelScope.launch {
            val fullNote = if (note.isNotBlank()) "[$selectedTrigger] $note" else "[$selectedTrigger]"
            val entry = MoodEntry(
                moodValue = moodValue,
                stressLevel = stressLevel,
                sleepHours = sleepHours,
                studyPressure = studyPressure,
                energyLevel = energyLevel,
                note = fullNote
            )
            wellbeingRepository.saveMoodEntry(entry)
            onComplete()
        }
    }
}
