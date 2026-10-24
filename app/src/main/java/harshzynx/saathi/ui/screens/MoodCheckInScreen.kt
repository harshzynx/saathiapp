package harshzynx.saathi.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodCheckInScreen(
    onBackClick: () -> Unit,
    viewModel: MoodCheckInViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Check-in & Trigger Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveMoodEntry(onBackClick) },
                icon = { Icon(Icons.Default.Check, contentDescription = null) },
                text = { Text("Save Check-in") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "How are you feeling today?", 
                fontWeight = FontWeight.ExtraBold, 
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Track your mood, stress meter, and primary trigger.", color = Color.Gray, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MoodEmojiSelector(
                selectedMood = viewModel.moodValue,
                onMoodSelected = { viewModel.moodValue = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Trigger Tracker
            Text("Primary Trigger Today", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val triggers = listOf("📚 Exams", "💔 Relationship", "🌧️ Loneliness", "🏡 Family", "💰 Money", "⚡ Other")
            Row(modifier = Modifier.fillMaxWidth()) {
                triggers.take(3).forEach { trigger ->
                    FilterChip(
                        selected = viewModel.selectedTrigger == trigger,
                        onClick = { viewModel.selectedTrigger = trigger },
                        label = { Text(trigger, fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                triggers.drop(3).forEach { trigger ->
                    FilterChip(
                        selected = viewModel.selectedTrigger == trigger,
                        onClick = { viewModel.selectedTrigger = trigger },
                        label = { Text(trigger, fontSize = 11.sp) },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CheckInSection(title = "Stress Meter (0–10)", subtitle = "${viewModel.stressLevel}/10") {
                Slider(
                    value = viewModel.stressLevel.toFloat(),
                    onValueChange = { viewModel.stressLevel = it.toInt() },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CheckInSection(title = "Sleep (Last Night)", subtitle = "${String.format(java.util.Locale.US, "%.1f", viewModel.sleepHours)} hours") {
                Slider(
                    value = viewModel.sleepHours,
                    onValueChange = { viewModel.sleepHours = it },
                    valueRange = 0f..12f
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Any private notes?", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.note,
                onValueChange = { viewModel.note = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Write about your day and what triggered this mood...") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CheckInSection(title: String, subtitle: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            content()
        }
    }
}

@Composable
fun MoodEmojiSelector(selectedMood: Int, onMoodSelected: (Int) -> Unit) {
    val emojis = listOf("😢", "☹️", "😐", "🙂", "😊")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        emojis.forEachIndexed { index, emoji ->
            val mood = index + 1
            val isSelected = selectedMood == mood
            val size by animateDpAsState(if (isSelected) 56.dp else 48.dp, label = "size")
            val bgColor by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent, label = "color")

            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onMoodSelected(mood) },
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = if (isSelected) 32.sp else 24.sp)
            }
        }
    }
}
