package harshzynx.saathi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.domain.GroqService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThoughtReframerViewModel @Inject constructor(
    private val groqService: GroqService
) : ViewModel() {

    var negativeThought by mutableStateOf("")
    var reframedThought by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun reframeThought() {
        if (negativeThought.isBlank()) return
        
        isLoading = true
        viewModelScope.launch {
            val prompt = "The student has this negative thought: '${negativeThought}'. " +
                    "Reframe it into a more balanced, positive, or compassionate perspective. " +
                    "Keep the response short and friendly in Hinglish."
            reframedThought = groqService.getChatResponse(prompt)
            isLoading = false
        }
    }

    fun reset() {
        negativeThought = ""
        reframedThought = null
    }
}
