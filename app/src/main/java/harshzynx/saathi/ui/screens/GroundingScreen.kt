package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroundingScreen(
    onBackClick: () -> Unit
) {
    var step by remember { mutableStateOf(0) }

    val steps = listOf(
        Triple("5 Things You Can See", "Look around you. Notice 5 objects you can see right now. Observe their colors, shapes, and textures.", "👀"),
        Triple("4 Things You Can Touch", "Notice 4 things you can physically feel—your clothes, the chair, your hair, the floor.", "✋"),
        Triple("3 Things You Can Hear", "Listen closely. Name 3 sounds around you—traffic, a fan, birds, or your breathing.", "👂"),
        Triple("2 Things You Can Smell", "Notice 2 smells around you—coffee, soap, fresh air, or your room's scent.", "👃"),
        Triple("1 Thing You Can Taste", "Notice 1 taste in your mouth—water, mint, or the lingering taste of your last meal.", "👅")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("5-4-3-2-1 Grounding 🌿") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step < steps.size) {
                val (title, desc, emoji) = steps[step]

                Card(
                    modifier = Modifier.fillMaxWidth().height(360.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = emoji, fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = desc,
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            lineHeight = 22.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step-- },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Button(
                        onClick = { step++ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(if (step == steps.size - 1) "Finish" else "Next Step")
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("You're Grounded ✨", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Take one final slow, deep breath. Notice how your body feels right now in the present moment.",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { step = 0 },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Restart Exercise")
                        }
                    }
                }
            }
        }
    }
}
