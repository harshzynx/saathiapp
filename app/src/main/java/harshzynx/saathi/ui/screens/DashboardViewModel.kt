package harshzynx.saathi.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.UsageAnalyticsManager
import harshzynx.saathi.data.local.ApiKeyManager
import harshzynx.saathi.data.model.Habit
import harshzynx.saathi.data.model.MoodEntry
import harshzynx.saathi.data.model.ParentalTask
import harshzynx.saathi.data.model.User
import harshzynx.saathi.data.repository.AuthRepository
import harshzynx.saathi.data.repository.WellbeingRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val wellbeingRepository: WellbeingRepository,
    private val authRepository: AuthRepository,
    private val usageAnalyticsManager: UsageAnalyticsManager,
    private val apiKeyManager: ApiKeyManager
) : ViewModel() {

    private val _moodHistory = mutableStateOf<List<MoodEntry>>(emptyList())
    val moodHistory: State<List<MoodEntry>> = _moodHistory

    private val _showSafetyAlert = mutableStateOf(false)
    val showSafetyAlert: State<Boolean> = _showSafetyAlert

    private val _showCaregiverAlert = mutableStateOf(false)
    val showCaregiverAlert: State<Boolean> = _showCaregiverAlert

    private val _showPositiveAlert = mutableStateOf(false)
    val showPositiveAlert: State<Boolean> = _showPositiveAlert

    private val _showUsageInsight = mutableStateOf<String?>(null)
    val showUsageInsight: State<String?> = _showUsageInsight

    private val _userProfile = mutableStateOf<User?>(null)
    val userProfile: State<User?> = _userProfile

    private val _wellnessScore = mutableFloatStateOf(0.1f)
    val wellnessScore: State<Float> = _wellnessScore

    private val _showComplianceDialog = mutableStateOf(!apiKeyManager.isComplianceAccepted()) 
    val showComplianceDialog: State<Boolean> = _showComplianceDialog

    val habits = mutableStateListOf<Habit>()
    val parentalTasks = mutableStateListOf<ParentalTask>()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _userProfile.value = authRepository.getUserProfile()
            loadMoodHistory()
            loadHabits()
            checkUsageInsights()
            generateParentalTasks()
        }
    }

    private fun checkUsageInsights() {
        if (usageAnalyticsManager.detectLateNightUsage()) {
            _showUsageInsight.value = "Late night usage detected. Sleep is crucial for recovery. 🌙"
        }
    }

    private fun generateParentalTasks() {
        parentalTasks.clear()
        parentalTasks.add(ParentalTask(1, "5-Min Chat", "Ask your child about their day without advice."))
        parentalTasks.add(ParentalTask(2, "Walk Together", "Go for a short 10-minute walk outside."))
        parentalTasks.add(ParentalTask(3, "Appreciation", "Note one small thing they did well today."))
    }

    private fun loadHabits() {
        viewModelScope.launch {
            val savedHabits = wellbeingRepository.getHabits()
            habits.clear()
            if (savedHabits.isEmpty()) {
                habits.addAll(listOf(
                    Habit(0, "Drink Water", false),
                    Habit(1, "5 min Sunlight", false),
                    Habit(2, "Stretch/Walk", false),
                ))
            } else {
                habits.addAll(savedHabits)
            }
        }
    }

    private fun loadMoodHistory() {
        viewModelScope.launch {
            val history = wellbeingRepository.getMoodEntries().take(7).reversed()
            _moodHistory.value = history
            _wellnessScore.floatValue = (history.size.toFloat() / 7f).coerceIn(0.1f, 1.0f)

            if (history.size >= 3) {
                val recentLowMoods = history.takeLast(3).all { it.moodValue <= 2 }
                if (recentLowMoods) _showSafetyAlert.value = true
                
                val criticalDistress = history.takeLast(3).all { it.moodValue == 1 }
                if (criticalDistress) _showCaregiverAlert.value = true
            }
        }
    }

    fun toggleHabit(index: Int) {
        habits[index] = habits[index].copy(isCompleted = !habits[index].isCompleted)
        viewModelScope.launch { wellbeingRepository.saveHabits(habits.toList()) }
        if (habits.all { it.isCompleted }) _showPositiveAlert.value = true
        updateWellnessScore()
    }

    private fun updateWellnessScore() {
        val completedCount = habits.count { it.isCompleted }
        _wellnessScore.floatValue = ((_moodHistory.value.size.toFloat() + completedCount) / 10f).coerceIn(0.1f, 1.0f)
    }

    fun dismissSafetyAlert() { _showSafetyAlert.value = false }
    fun dismissCaregiverAlert() { _showCaregiverAlert.value = false }
    fun dismissPositiveAlert() { _showPositiveAlert.value = false }
    fun dismissUsageInsight() { _showUsageInsight.value = null }
    fun acceptCompliance() {
        apiKeyManager.setComplianceAccepted(true)
        _showComplianceDialog.value = false
    }
}
