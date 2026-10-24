package harshzynx.saathi.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import harshzynx.saathi.data.model.Habit
import harshzynx.saathi.data.model.MoodEntry
import harshzynx.saathi.data.model.ParentalTask
import harshzynx.saathi.data.model.User
import harshzynx.saathi.ui.components.ComplianceDialog
import harshzynx.saathi.ui.components.SafetyHelplineDialog
import harshzynx.saathi.ui.components.SupportLadderDialog
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart
import java.util.Calendar

@Composable
fun DashboardScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToMoodCheckIn: () -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToExamStress: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToThoughtReframer: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToCounselor: () -> Unit,
    onNavigateToAssessment: () -> Unit,
    onNavigateToGrounding: () -> Unit,
    onNavigateToSafetyPlan: () -> Unit,
    onNavigateToRecoveryLibrary: () -> Unit,
    onNavigateToOverthinking: () -> Unit,
    onNavigateToTeamSupport: () -> Unit,
    onNavigateToMoodAnalytics: () -> Unit,
    onNavigateToStreaksBadges: () -> Unit,
    onNavigateToAnimeAvatar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val moodHistory by viewModel.moodHistory
    val autoSafetyAlert by viewModel.showSafetyAlert
    val wellnessScore by viewModel.wellnessScore
    val habits = viewModel.habits
    val showCaregiverAlert by viewModel.showCaregiverAlert
    val showPositiveAlert by viewModel.showPositiveAlert
    val showComplianceDialog by viewModel.showComplianceDialog
    val usageInsight by viewModel.showUsageInsight
    val userProfile by viewModel.userProfile
    val parentalTasks = viewModel.parentalTasks

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    DashboardContent(
        moodHistory = moodHistory,
        autoSafetyAlert = autoSafetyAlert,
        showCaregiverAlert = showCaregiverAlert,
        showPositiveAlert = showPositiveAlert,
        showComplianceDialog = showComplianceDialog,
        usageInsight = usageInsight,
        userProfile = userProfile,
        wellnessScore = wellnessScore,
        habits = habits,
        parentalTasks = parentalTasks,
        onToggleHabit = { viewModel.toggleHabit(it) },
        onRefresh = { viewModel.loadData() },
        onDismissSafetyAlert = { viewModel.dismissSafetyAlert() },
        onDismissCaregiverAlert = { viewModel.dismissCaregiverAlert() },
        onDismissPositiveAlert = { viewModel.dismissPositiveAlert() },
        onDismissUsageInsight = { viewModel.dismissUsageInsight() },
        onAcceptCompliance = { viewModel.acceptCompliance() },
        onNavigateToChat = onNavigateToChat,
        onNavigateToMoodCheckIn = onNavigateToMoodCheckIn,
        onNavigateToActivities = onNavigateToActivities,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToExamStress = onNavigateToExamStress,
        onNavigateToJournal = onNavigateToJournal,
        onNavigateToThoughtReframer = onNavigateToThoughtReframer,
        onNavigateToPrivacy = onNavigateToPrivacy,
        onNavigateToCommunity = onNavigateToCommunity,
        onNavigateToCounselor = onNavigateToCounselor,
        onNavigateToAssessment = onNavigateToAssessment,
        onNavigateToGrounding = onNavigateToGrounding,
        onNavigateToSafetyPlan = onNavigateToSafetyPlan,
        onNavigateToRecoveryLibrary = onNavigateToRecoveryLibrary,
        onNavigateToOverthinking = onNavigateToOverthinking,
        onNavigateToTeamSupport = onNavigateToTeamSupport,
        onNavigateToMoodAnalytics = onNavigateToMoodAnalytics,
        onNavigateToStreaksBadges = onNavigateToStreaksBadges,
        onNavigateToAnimeAvatar = onNavigateToAnimeAvatar,
    )
}

@Composable
fun DashboardContent(
    moodHistory: List<MoodEntry>,
    autoSafetyAlert: Boolean,
    showCaregiverAlert: Boolean,
    showPositiveAlert: Boolean,
    showComplianceDialog: Boolean,
    usageInsight: String?,
    userProfile: User?,
    wellnessScore: Float,
    habits: List<Habit>,
    parentalTasks: List<ParentalTask>,
    onToggleHabit: (Int) -> Unit,
    onRefresh: () -> Unit,
    onDismissSafetyAlert: () -> Unit,
    onDismissCaregiverAlert: () -> Unit,
    onDismissPositiveAlert: () -> Unit,
    onDismissUsageInsight: () -> Unit,
    onAcceptCompliance: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToMoodCheckIn: () -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToExamStress: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToThoughtReframer: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToCounselor: () -> Unit,
    onNavigateToAssessment: () -> Unit,
    onNavigateToGrounding: () -> Unit,
    onNavigateToSafetyPlan: () -> Unit,
    onNavigateToRecoveryLibrary: () -> Unit,
    onNavigateToOverthinking: () -> Unit,
    onNavigateToTeamSupport: () -> Unit,
    onNavigateToMoodAnalytics: () -> Unit,
    onNavigateToStreaksBadges: () -> Unit,
    onNavigateToAnimeAvatar: () -> Unit,
) {
    var showSafetyDialog by remember { mutableStateOf(false) }
    var showSupportLadder by remember { mutableStateOf(false) }
    var showWhatDoYouNeed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showSupportLadder) {
        SupportLadderDialog(
            onDismiss = { showSupportLadder = false },
            onNavigateToGrounding = onNavigateToGrounding,
            onNavigateToJournal = onNavigateToJournal,
            onNavigateToSafetyPlan = onNavigateToSafetyPlan,
            onNavigateToCounselor = onNavigateToCounselor,
            onCallEmergency = { showSafetyDialog = true }
        )
    }

    if (showWhatDoYouNeed) {
        harshzynx.saathi.ui.components.WhatDoYouNeedDialog(
            onDismiss = { showWhatDoYouNeed = false },
            onNavigateToActivities = onNavigateToActivities,
            onNavigateToChat = onNavigateToChat,
            onNavigateToJournal = onNavigateToJournal,
            onNavigateToOverthinking = onNavigateToOverthinking,
            onNavigateToGrounding = onNavigateToGrounding
        )
    }

    val greetingText = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤️"
            else -> "Good Evening 🌙"
        }
    }

    if (showComplianceDialog) {
        ComplianceDialog(onAccept = onAcceptCompliance)
    }

    if (showSafetyDialog || autoSafetyAlert) {
        SafetyHelplineDialog(onDismiss = { 
            showSafetyDialog = false 
            onDismissSafetyAlert()
        })
    }

    Scaffold(
        floatingActionButton = {
            SmallFloatingActionButton(
                onClick = { 
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Quick Exit")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 0.dp, bottom = 28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Top Header (Greeting + Energy Badge + Shield + Profile)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greetingText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userProfile?.name?.ifBlank { "Saathi Friend" } ?: "Saathi Friend",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Take a deep breath. You're doing great ✨",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Energy Badge / Streaks
                        Surface(
                            onClick = onNavigateToStreaksBadges,
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("7 🔥", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }

                        // Shield Security Icon
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            IconButton(onClick = onNavigateToPrivacy, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Shield, contentDescription = "Privacy", tint = Color(0xFF00796B), modifier = Modifier.size(18.dp))
                            }
                        }

                        // Profile Icon
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.size(40.dp)
                        ) {
                            IconButton(onClick = onNavigateToProfile) {
                                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                if (userProfile == null || userProfile.parentMobile.isNullOrBlank() || userProfile.institutionName.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57C00))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Complete Your Profile ✨", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                                Text("Add your college & parent contact for safety features.", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFF57C00))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Signature "What Do You Need Right Now?" Gradient Card
                Card(
                    onClick = { showWhatDoYouNeed = true },
                    modifier = Modifier.fillMaxWidth().height(84.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF00796B),
                                        Color(0xFF1E3A8A)
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text("What do you need right now?", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Talk  •  Breathe  •  Be Heard  •  You're Not Alone", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DailyInspirationCard()

                Spacer(modifier = Modifier.height(20.dp))

                // Daily Self-Care Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Daily Self-Care ✨", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                                Text("Consistency builds peace", fontSize = 12.sp, color = Color.Gray)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF388E3C), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val completedCount = habits.count { it.isCompleted }
                                    Text("Today $completedCount / ${habits.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val habitSubtitles = listOf(
                            "Stay hydrated, stay focused.",
                            "Natural light boosts your mood.",
                            "Move a little, feel a lot better."
                        )

                        habits.forEachIndexed { index, habit ->
                            val subText = habitSubtitles.getOrElse(index) { "Daily healthy habit." }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        val icon = when(index) {
                                            0 -> Icons.Default.WaterDrop
                                            1 -> Icons.Default.WbSunny
                                            else -> Icons.AutoMirrored.Filled.DirectionsWalk
                                        }
                                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(habit.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                    Text(subText, fontSize = 11.sp, color = Color.Gray)
                                }

                                Checkbox(
                                    checked = habit.isCompleted,
                                    onCheckedChange = { onToggleHabit(index) },
                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mood Rhythm & Triggers Card (with link to Analytics)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Mood Rhythm & Triggers 📈", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                                Text("Track your mood • Find patterns • Feel more in control", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = onNavigateToMoodAnalytics,
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE0F2F1),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Analytics", fontSize = 10.sp, color = Color.Gray)
                                        Text("View Trends 📊", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Button(
                                onClick = onNavigateToMoodCheckIn,
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Check in ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Explore Saathi Hub", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(12.dp))

                // Rich Hub Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    GradientActionCard(
                        title = "Anime Guardian",
                        subtitle = "Equip Companion ⚔️",
                        icon = Icons.Default.Face,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFCE93D8), Color(0xFF7E57C2)),
                        onClick = onNavigateToAnimeAvatar
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GradientActionCard(
                        title = "Talk to Our Team",
                        subtitle = "Live support desk",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF80CBC4), Color(0xFF00796B)),
                        onClick = onNavigateToTeamSupport
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GradientActionCard(
                        title = "Mood Analytics",
                        subtitle = "Weekly trends 📊",
                        icon = Icons.Default.BarChart,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFF81D4FA), Color(0xFF0288D1)),
                        onClick = onNavigateToMoodAnalytics
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GradientActionCard(
                        title = "Badges & Streaks",
                        subtitle = "Unlocked awards 🏆",
                        icon = Icons.Default.EmojiEvents,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFFFE082), Color(0xFFF57C00)),
                        onClick = onNavigateToStreaksBadges
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GradientActionCard(
                        title = "PHQ-9 Test",
                        subtitle = "Self assessment",
                        icon = Icons.Default.Assessment,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFA78BFA), Color(0xFF6D28D9)),
                        onClick = onNavigateToAssessment
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GradientActionCard(
                        title = "I Need Help",
                        subtitle = "Support Ladder",
                        icon = Icons.Default.SupportAgent,
                        modifier = Modifier.weight(1f),
                        gradientColors = listOf(Color(0xFFEF9A9A), Color(0xFFC62828)),
                        onClick = { showSupportLadder = true }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun DailyInspirationCard() {
    val quotes = listOf(
        "Chill maar, exams hi toh hain! You've got this. ✨",
        "Aaj ka din sirf tumhara hai. Stay positive! 🌿",
        "Don't be hard on yourself. Ek break banta hai. ☕",
        "Success takes time. Be patient with yourself. ⏳"
    )
    val randomQuote = remember { quotes.random() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Lightbulb, 
                        contentDescription = null, 
                        tint = Color(0xFF388E3C),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = randomQuote,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "You've got this. ✨",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color(0xFF7E57C2), modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun GradientActionCard(
    title: String, 
    subtitle: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    gradientColors: List<Color>, 
    modifier: Modifier, 
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                Text(subtitle, fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}
