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
fun WhatDoYouNeedDialog(
    onDismiss: () -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToOverthinking: () -> Unit,
    onNavigateToGrounding: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("💙 What do you need right now?", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select your immediate intention, and Saathi will guide you.", fontSize = 12.sp, color = Color.Gray)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(8.dp))

                NeedOptionCard(
                    title = "Calm My Body",
                    subtitle = "60-sec breathing or 5-4-3-2-1 grounding",
                    icon = Icons.Default.SelfImprovement,
                    color = Color(0xFFE8F5E9),
                    onClick = {
                        onDismiss()
                        onNavigateToGrounding()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeedOptionCard(
                    title = "Stop Overthinking",
                    subtitle = "Separate Fact vs Fear and find next micro-step",
                    icon = Icons.Default.Psychology,
                    color = Color(0xFFFCE4EC),
                    onClick = {
                        onDismiss()
                        onNavigateToOverthinking()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeedOptionCard(
                    title = "Talk to Someone (AI)",
                    subtitle = "Vent or chat in a supportive Hinglish space",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFFF3E5F5),
                    onClick = {
                        onDismiss()
                        onNavigateToChat()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeedOptionCard(
                    title = "Write It Down",
                    subtitle = "Pour your thoughts into your private journal",
                    icon = Icons.Default.EditNote,
                    color = Color(0xFFE3F2FD),
                    onClick = {
                        onDismiss()
                        onNavigateToJournal()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                NeedOptionCard(
                    title = "Relax & Focus",
                    subtitle = "Soothing sounds and meditation timer",
                    icon = Icons.Default.Timer,
                    color = Color(0xFFE0F2F1),
                    onClick = {
                        onDismiss()
                        onNavigateToActivities()
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
fun NeedOptionCard(
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
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(26.dp), tint = Color.DarkGray)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
