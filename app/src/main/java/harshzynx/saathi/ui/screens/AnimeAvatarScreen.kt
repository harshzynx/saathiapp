package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.data.local.AnimeAvatarManager
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeAvatarScreen(
    onBackClick: () -> Unit,
    avatarManager: AnimeAvatarManager
) {
    val context = LocalContext.current
    var selectedId by remember { mutableStateOf(avatarManager.getSelectedAvatarId()) }
    var currentSpeech by remember { mutableStateOf<String?>(null) }

    val activeAvatar = avatarManager.availableAvatars.find { it.id == selectedId } ?: avatarManager.availableAvatars[0]

    // Pulsing aura animation
    val infiniteTransition = rememberInfiniteTransition(label = "aura")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anime Companion Avatar ⚔️✨") },
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFF3E5F5), GradientEnd)))
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Choose Your Anime Guardian", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("Equip an anime companion to guide you through tough days ✨", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Main Anime Avatar Display Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing Glowing Aura
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .scale(scale)
                            .background(Color(activeAvatar.auraColorHex).copy(alpha = 0.25f), CircleShape)
                            .clickable {
                                currentSpeech = activeAvatar.quote
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(activeAvatar.auraColorHex),
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(activeAvatar.emoji, fontSize = 54.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(activeAvatar.name, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    Text(activeAvatar.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(activeAvatar.auraColorHex))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speech Bubble
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = currentSpeech ?: "\"Tap me to hear my message for you! 🗣️\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Character Base", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))

            // Avatar Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                avatarManager.availableAvatars.forEach { avatar ->
                    val isSelected = avatar.id == selectedId
                    Card(
                        onClick = {
                            selectedId = avatar.id
                            currentSpeech = avatar.quote
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFE0F2F1) else Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(avatar.emoji, fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(avatar.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Equip Button
            Button(
                onClick = {
                    avatarManager.saveSelectedAvatarId(selectedId)
                    Toast.makeText(context, "${activeAvatar.name} Equipped as your Companion! ✨", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Equip ${activeAvatar.name} ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
