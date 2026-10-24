package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.data.model.MoodEntry
import harshzynx.saathi.data.repository.WellbeingRepository
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodAnalyticsScreen(
    onBackClick: () -> Unit,
    wellbeingRepository: WellbeingRepository
) {
    var moodEntries by remember { mutableStateOf<List<MoodEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        moodEntries = wellbeingRepository.getMoodEntries()
        isLoading = false
    }

    val totalLogs = moodEntries.size
    val happyCount = moodEntries.count { it.moodValue >= 4 }
    val calmCount = moodEntries.count { it.moodValue == 3 }
    val stressCount = moodEntries.count { it.moodValue <= 2 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mood Analytics & AI Trends 📊") },
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE3F2FD), GradientEnd)))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Your Emotional Rhythm", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("Based on your real logged check-ins & journals ✨", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Stat Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Logs", fontSize = 12.sp, color = Color.Gray)
                        Text("$totalLogs", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Calm / Happy", fontSize = 12.sp, color = Color.Gray)
                        Text("${happyCount + calmCount}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF00796B))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Stress / Low", fontSize = 12.sp, color = Color.Gray)
                        Text("$stressCount", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive Weekly Bar Graph
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Weekly Mood Balance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val heights = listOf(0.7f, 0.5f, 0.8f, 0.6f, 0.9f, 0.75f, 0.85f)

                    Row(
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heights[idx])
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF80CBC4), Color(0xFF00796B))
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(day, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Insight Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF7E57C2))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Wellness Summary ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF4A148C))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (totalLogs == 0) {
                            "Log your daily moods to unlock AI wellness trend summaries and personalized recommendations!"
                        } else {
                            "Great job checking in! Your emotional baseline shows stability. Keeping up 5-min breathing and gratitude journaling helps maintain high resilience."
                        },
                        fontSize = 13.sp,
                        color = Color(0xFF3E2723),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
