package harshzynx.saathi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamStressScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academic & Career Support") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Focus Timer (Pomodoro)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Study/Work for 25 mins, then take a 5 min break.", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PomodoroTimer()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Success & Wellbeing Tips", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            StudyTipCard("Break it Down", "Whether it's a chapter or a work project, divide it into small tasks.")
            StudyTipCard("Consistency > Intensity", "Small daily efforts lead to big results without burnout.")
            StudyTipCard("Mindful Breaks", "In your break, step away from screens. Breathe and stretch.")
        }
    }
}

@Composable
fun PomodoroTimer() {
    var timeLeft by remember { mutableLongStateOf(25 * 60 * 1000L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1000
        }
    }

    val minutes = (timeLeft / 1000) / 60
    val seconds = (timeLeft / 1000) % 60

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds),
            fontSize = 64.sp,
            fontWeight = FontWeight.Light
        )
        
        Row {
            IconButton(onClick = { isRunning = !isRunning }) {
                Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Start/Pause")
            }
            IconButton(onClick = { 
                isRunning = false
                timeLeft = 25 * 60 * 1000L 
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
            }
        }
    }
}

@Composable
fun StudyTipCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(description, fontSize = 14.sp)
        }
    }
}
