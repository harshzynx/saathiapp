package harshzynx.saathi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import harshzynx.saathi.data.local.AnimeAvatarManager
import harshzynx.saathi.data.repository.AuthRepository
import harshzynx.saathi.data.repository.WellbeingRepository
import harshzynx.saathi.ui.screens.*
import harshzynx.saathi.ui.theme.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {
    @Inject
    lateinit var wellbeingRepository: WellbeingRepository

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var animeAvatarManager: AnimeAvatarManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SaathiTheme {
                SaathiAppContent(wellbeingRepository, authRepository, animeAvatarManager)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("dashboard", "Home", Icons.Default.Home)
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Activities : Screen("activities", "Activities", Icons.Default.SelfImprovement)
    object Journal : Screen("journal", "Journal", Icons.Default.Book)
    object Settings : Screen("profile", "Settings", Icons.Default.Settings)
}

@Composable
fun SaathiAppContent(
    wellbeingRepository: WellbeingRepository,
    authRepository: AuthRepository,
    animeAvatarManager: AnimeAvatarManager
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Chat,
        Screen.Activities,
        Screen.Journal,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar only on main tab routes
    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title, fontSize = 11.sp) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") {
                    SplashScreen(onAnimationFinished = { destination ->
                        navController.navigate(destination) {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }
                composable("team_inbox") {
                    TeamInboxScreen(
                        onLogout = {
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                composable("auth") {
                    AuthScreen(
                        onLoginSuccess = {
                            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                            val email = firebaseUser?.email?.trim()?.lowercase() ?: ""

                            if (email == "harshsharma18089@gmail.com") {
                                navController.navigate("admin_panel") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            } else if (email.isNotBlank()) {
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    .collection("team_staff")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        if (snapshot != null && !snapshot.isEmpty) {
                                            navController.navigate("team_inbox") {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    }
                            } else {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }
                    )
                }
                composable("admin_panel") {
                    AdminTeamManagementScreen(
                        onNavigateToInbox = { navController.navigate("team_inbox") },
                        onLogout = {
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                composable("onboarding") {
                    OnboardingScreen(
                        onFinished = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo("onboarding") { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Home.route) {
                    DashboardScreen(
                        onNavigateToChat = { navController.navigate(Screen.Chat.route) },
                        onNavigateToMoodCheckIn = { navController.navigate("mood_checkin") },
                        onNavigateToActivities = { navController.navigate(Screen.Activities.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Settings.route) },
                        onNavigateToExamStress = { navController.navigate("exam_stress") },
                        onNavigateToJournal = { navController.navigate(Screen.Journal.route) },
                        onNavigateToThoughtReframer = { navController.navigate("thought_reframer") },
                        onNavigateToPrivacy = { navController.navigate("privacy") },
                        onNavigateToCommunity = { navController.navigate(Screen.Home.route) },
                        onNavigateToCounselor = { navController.navigate("counselor_link") },
                        onNavigateToAssessment = { navController.navigate("self_assessment") },
                        onNavigateToGrounding = { navController.navigate("grounding") },
                        onNavigateToSafetyPlan = { navController.navigate("safety_plan") },
                        onNavigateToRecoveryLibrary = { navController.navigate("recovery_library") },
                        onNavigateToOverthinking = { navController.navigate("overthinking") },
                        onNavigateToTeamSupport = { navController.navigate("team_support") },
                        onNavigateToMoodAnalytics = { navController.navigate("mood_analytics") },
                        onNavigateToStreaksBadges = { navController.navigate("streaks_badges") },
                        onNavigateToAnimeAvatar = { navController.navigate("anime_avatar") }
                    )
                }
                composable("anime_avatar") {
                    AnimeAvatarScreen(
                        onBackClick = { navController.popBackStack() },
                        avatarManager = animeAvatarManager
                    )
                }
                composable("mood_analytics") {
                    MoodAnalyticsScreen(
                        onBackClick = { navController.popBackStack() },
                        wellbeingRepository = wellbeingRepository
                    )
                }
                composable("streaks_badges") {
                    StreaksAndBadgesScreen(
                        onBackClick = { navController.popBackStack() },
                        wellbeingRepository = wellbeingRepository,
                        authRepository = authRepository
                    )
                }
                composable(Screen.Chat.route) {
                    ChatScreen(onBackClick = { navController.popBackStack() })
                }
                composable("mood_checkin") {
                    MoodCheckInScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Screen.Activities.route) {
                    ActivitiesScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToSounds = { navController.navigate("sounds") }
                    )
                }
                composable("sounds") {
                    RelaxationAudioPlayer(onBackClick = { navController.popBackStack() })
                }
                composable(Screen.Settings.route) {
                    ProfileScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToGuardianLink = { navController.navigate("guardian_link") },
                        onNavigateToParentalGuidance = { navController.navigate("parental_guidance") },
                        onLogout = {
                            navController.navigate("auth") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                composable("guardian_link") {
                    GuardianLinkScreen(onBackClick = { navController.popBackStack() })
                }
                composable("parental_guidance") {
                    ParentalGuidanceScreen(onBackClick = { navController.popBackStack() })
                }
                composable("exam_stress") {
                    ExamStressScreen(onBackClick = { navController.popBackStack() })
                }
                composable(Screen.Journal.route) {
                    JournalScreen(onBackClick = { navController.popBackStack() })
                }
                composable("thought_reframer") {
                    ThoughtReframerScreen(onBackClick = { navController.popBackStack() })
                }
                composable("privacy") {
                    PrivacySettingsScreen(onBackClick = { navController.popBackStack() })
                }
                composable("counselor_link") {
                    CounselorLinkScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToTeamSupport = { navController.navigate("team_support") }
                    )
                }
                composable("team_support") {
                    TeamSupportHubScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToChat = { name -> navController.navigate("team_chat/$name") },
                        onNavigateToRoom = { roomTitle -> navController.navigate("team_chat/$roomTitle") }
                    )
                }
                composable("team_chat/{name}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: "Support Team"
                    TeamChatScreen(memberName = name, onBackClick = { navController.popBackStack() })
                }
                composable("self_assessment") {
                    SelfAssessmentScreen(
                        onBackClick = { navController.popBackStack() },
                        onNavigateToSOS = { navController.popBackStack() }
                    )
                }
                composable("grounding") {
                    GroundingScreen(onBackClick = { navController.popBackStack() })
                }
                composable("safety_plan") {
                    SafetyPlanScreen(onBackClick = { navController.popBackStack() })
                }
                composable("recovery_library") {
                    RecoveryLibraryScreen(onBackClick = { navController.popBackStack() })
                }
                composable("overthinking") {
                    OverthinkingBreakerScreen(onBackClick = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
fun SaathiTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = CalmTeal,
        secondary = PremiumPurple,
        tertiary = SoftBlue,
        background = Color.White,
        surface = Color.White,
        error = WarningRed
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
