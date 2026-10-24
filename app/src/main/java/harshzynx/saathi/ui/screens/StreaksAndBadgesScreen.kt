package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.data.repository.AuthRepository
import harshzynx.saathi.data.repository.WellbeingRepository
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

data class BadgeItem(
    val title: String,
    val desc: String,
    val emoji: String,
    val isUnlocked: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreaksAndBadgesScreen(
    onBackClick: () -> Unit,
    wellbeingRepository: WellbeingRepository,
    authRepository: AuthRepository
) {
    var moodCount by remember { mutableIntStateOf(0) }
    var isGuardianLinked by remember { mutableStateOf(false) }
    var streakDays by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        val moods = wellbeingRepository.getMoodEntries()
        moodCount = moods.size
        isGuardianLinked = authRepository.getUserProfile()?.isGuardianLinked == true
        streakDays = if (moods.isNotEmpty()) (moods.size.coerceAtLeast(1)) else 1
    }

    val badges = listOf(
        BadgeItem("First Step", "Logged first mood check-in", "🌱", moodCount >= 1),
        BadgeItem("3-Day Streak", "Active 3 consecutive days", "🔥", streakDays >= 3),
        BadgeItem("7-Day Champion", "Active 7 consecutive days", "🏆", streakDays >= 7),
        BadgeItem("Mind Gamer", "Played Level 1 Mind Games", "🧩", true),
        BadgeItem("Safety Net", "Linked trusted guardian contact", "🛡️", isGuardianLinked),
        BadgeItem("Storyteller", "Wrote 1+ Journal entries", "📖", moodCount >= 1),
        BadgeItem("Zen Master", "Completed 14-Day Streak", "🧘", streakDays >= 14),
        BadgeItem("Peace Finder", "Completed 30-Day Streak", "🌟", streakDays >= 30)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Streaks & Badges 🏆") },
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE8F5E9), GradientEnd)))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Streak Header Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("$streakDays Days Active Streak! 🔥", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("You're building great mindful habits!", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Unlocked Badges (${badges.count { it.isUnlocked }} / ${badges.size}) 🏅", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(12.dp))

            // Badges Grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                badges.chunked(2).forEach { rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            Card(
                                modifier = Modifier.weight(1f).height(120.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (badge.isUnlocked) Color.White else Color(0xFFF1F5F9)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize().padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = badge.emoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = badge.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (badge.isUnlocked) Color(0xFF1E293B) else Color.Gray, textAlign = TextAlign.Center)
                                    Text(text = badge.desc, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 12.sp)
                                }
                            }
                        }
                        if (rowBadges.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
