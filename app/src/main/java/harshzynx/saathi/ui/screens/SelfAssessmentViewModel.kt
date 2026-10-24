package harshzynx.saathi.ui.screens

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelfAssessmentViewModel @Inject constructor() : ViewModel() {

    // Question index (0 to 8) mapped to selected score (0 to 3)
    val answers = mutableStateMapOf<Int, Int>()

    fun setAnswer(questionIndex: Int, score: Int) {
        answers[questionIndex] = score
    }

    fun calculateScore(): Int {
        return answers.values.sum()
    }

    fun getSeverity(score: Int): Pair<String, String> {
        return when {
            score <= 4 -> Pair("Minimal Depression", "Your score suggests minimal or no depression. Keep up your positive habits!")
            score <= 9 -> Pair("Mild Depression", "You may have mild depressive symptoms. Try using Saathi's breathing exercises and journal.")
            score <= 14 -> Pair("Moderate Depression", "Moderate symptoms detected. Regular check-ins and talking to our AI companion or a trusted mentor can be very helpful.")
            score <= 19 -> Pair("Moderately Severe", "Moderately severe symptoms. Consider connecting with a professional counselor or reaching out to your linked guardian.")
            else -> Pair("Severe Symptoms", "Severe symptoms detected. Please reach out for immediate professional support or call Tele-MANAS at 14416.")
        }
    }

    fun reset() {
        answers.clear()
    }
}
