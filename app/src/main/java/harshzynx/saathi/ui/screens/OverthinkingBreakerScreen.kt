package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverthinkingBreakerScreen(
    onBackClick: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var negativeThought by remember { mutableStateOf("") }
    var factVsFear by remember { mutableStateOf("") }
    var inControl by remember { mutableStateOf("") }
    var nextStep by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Overthinking Breaker 🧠") },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    BreakerStepCard(
                        title = "Step 1: Write Down the Racing Thought",
                        subtitle = "What is the primary thought looping in your head right now?",
                        value = negativeThought,
                        onValueChange = { negativeThought = it },
                        placeholder = "e.g., My exam is tomorrow and I'm going to fail...",
                        onNext = { if (negativeThought.isNotBlank()) step = 2 }
                    )
                }
                2 -> {
                    BreakerStepCard(
                        title = "Step 2: Separate Fact vs. Fear",
                        subtitle = "What is an objective fact here, and what is your fear talking?",
                        value = factVsFear,
                        onValueChange = { factVsFear = it },
                        placeholder = "Fact: I have studied 4 chapters. Fear: I will forget everything...",
                        onNext = { if (factVsFear.isNotBlank()) step = 3 }
                    )
                }
                3 -> {
                    BreakerStepCard(
                        title = "Step 3: What is in Your Control?",
                        subtitle = "You can't control the whole exam paper, but what can you do right now?",
                        value = inControl,
                        onValueChange = { inControl = it },
                        placeholder = "I can revise summary notes for 30 minutes and sleep well.",
                        onNext = { if (inControl.isNotBlank()) step = 4 }
                    )
                }
                4 -> {
                    BreakerStepCard(
                        title = "Step 4: Your Next Micro-Step",
                        subtitle = "What is one extremely small, manageable action you can take in the next 5 minutes?",
                        value = nextStep,
                        onValueChange = { nextStep = it },
                        placeholder = "Drink a glass of water and open chapter 5 notes.",
                        onNext = { step = 5 },
                        buttonText = "Complete Exercise"
                    )
                }
                5 -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Thought Unlocked ✨", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "You've successfully broken the mental loop. Focus only on your micro-step: '$nextStep'.",
                                fontSize = 14.sp,
                                color = Color.DarkGray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBackClick,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Return to Dashboard")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BreakerStepCard(
    title: String,
    subtitle: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onNext: () -> Unit,
    buttonText: String = "Next Step"
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = subtitle, fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = value.isNotBlank()
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
