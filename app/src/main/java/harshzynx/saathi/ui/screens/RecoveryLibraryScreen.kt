package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

data class RecoveryArticle(
    val category: String,
    val title: String,
    val summary: String,
    val steps: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoveryLibraryScreen(
    onBackClick: () -> Unit
) {
    val articles = listOf(
        RecoveryArticle(
            category = "📚 Exam Stress",
            title = "Overcoming Academic Pressure & Burnout",
            summary = "Exams do not define your self-worth. Here is how to manage study anxiety effectively.",
            steps = listOf(
                "Break large chapters into tiny 20-minute micro-tasks.",
                "Practice the Pomodoro technique (25 min study, 5 min break).",
                "Remember to hydrate and take proper 7-8 hours of sleep before exam day."
            )
        ),
        RecoveryArticle(
            category = "🌧️ Loneliness",
            title = "Navigating Feeling Alone in College/School",
            summary = "Feeling isolated is tough, but connection is closer than you think.",
            steps = listOf(
                "Reach out to one old friend or family member with a simple 'Hi'.",
                "Join a campus club, library study group, or hobby community.",
                "Remind yourself that solitude is different from loneliness; use this time for self-care."
            )
        ),
        RecoveryArticle(
            category = "💔 Breakup / Heartbreak",
            title = "Healing a Broken Heart Step-by-Step",
            summary = "Allow yourself to feel the grief without self-judgment.",
            steps = listOf(
                "Give yourself permission to cry and grieve the loss.",
                "Practice digital detox: mute or unfollow to create breathing room.",
                "Redirect energy into physical workouts, journaling, or creative hobbies."
            )
        ),
        RecoveryArticle(
            category = "🌀 Overthinking",
            title = "How to Stop the Mental Loop (Rumination)",
            summary = "Your thoughts are not facts. Break the cycle of endless worrying.",
            steps = listOf(
                "Write your worries down on paper to get them out of your head.",
                "Use the 5-4-3-2-1 grounding technique to return to the present moment.",
                "Ask yourself: 'Will this matter 5 years from now?'"
            )
        ),
        RecoveryArticle(
            category = "🌙 Sleep Routine",
            title = "Achieving Deep, Restful Sleep",
            summary = "Good sleep is the foundation of emotional resilience.",
            steps = listOf(
                "Turn off screens and bright lights 45 minutes before bedtime.",
                "Keep your bedroom cool, dark, and quiet.",
                "Do a 3-minute gratitude journal check-in before closing your eyes."
            )
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recovery Library 📖") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "Expert-backed coping guides for specific life situations. Take your time reading through them.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(articles) { article ->
                    RecoveryArticleCard(article)
                }
            }
        }
    }
}

@Composable
fun RecoveryArticleCard(article: RecoveryArticle) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = article.category, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = article.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = article.summary, fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            article.steps.forEachIndexed { index, step ->
                Text(text = "• $step", fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
            }
        }
    }
}
