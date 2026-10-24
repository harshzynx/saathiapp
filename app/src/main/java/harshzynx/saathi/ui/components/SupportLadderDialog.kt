package harshzynx.saathi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupportLadderDialog(
    onDismiss: () -> Unit,
    onNavigateToGrounding: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToSafetyPlan: () -> Unit,
    onNavigateToCounselor: () -> Unit,
    onCallEmergency: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Support Ladder 🛟", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Take a deep breath. Choose the level of support you need right now:",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                SupportLevelCard(
                    level = "Level 1",
                    title = "Calm Myself",
                    subtitle = "Do a quick 5-4-3-2-1 grounding or breathing exercise",
                    icon = Icons.Default.SelfImprovement,
                    color = Color(0xFFE8F5E9),
                    onClick = {
                        onDismiss()
                        onNavigateToGrounding()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SupportLevelCard(
                    level = "Level 2",
                    title = "Write It Down",
                    subtitle = "Pour your thoughts safely into your private journal",
                    icon = Icons.Default.EditNote,
                    color = Color(0xFFE3F2FD),
                    onClick = {
                        onDismiss()
                        onNavigateToJournal()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SupportLevelCard(
                    level = "Level 3",
                    title = "Contact Trusted Person",
                    subtitle = "Open your personal safety plan or send 'Call Me' request",
                    icon = Icons.Default.Shield,
                    color = Color(0xFFFFEBEE),
                    onClick = {
                        onDismiss()
                        onNavigateToSafetyPlan()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SupportLevelCard(
                    level = "Level 4",
                    title = "Professional Support",
                    subtitle = "Connect with your institution counselor or directory",
                    icon = Icons.Default.School,
                    color = Color(0xFFE1F5FE),
                    onClick = {
                        onDismiss()
                        onNavigateToCounselor()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SupportLevelCard(
                    level = "Level 5",
                    title = "Immediate Emergency Help",
                    subtitle = "Call Tele-MANAS expert helpline (14416) right away",
                    icon = Icons.Default.Emergency,
                    color = Color(0xFFFFCDD2),
                    onClick = {
                        onDismiss()
                        onCallEmergency()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun SupportLevelCard(
    level: String,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.DarkGray)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(level, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                }
                Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
