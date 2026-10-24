package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfAssessmentScreen(
    onBackClick: () -> Unit,
    onNavigateToSOS: () -> Unit,
    viewModel: SelfAssessmentViewModel = hiltViewModel()
) {
    var showResult by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf(0) }

    val questions = listOf(
        "Little interest or pleasure in doing things",
        "Feeling down, depressed, or hopeless",
        "Trouble falling or staying asleep, or sleeping too much",
        "Feeling tired or having little energy",
        "Poor appetite or overeating",
        "Feeling bad about yourself — or that you are a failure",
        "Trouble concentrating on things, such as reading or studying",
        "Moving or speaking so slowly that other people could have noticed",
        "Thoughts that you would be better off dead, or hurting yourself"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PHQ-9 Mental Health Test 🧠") },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showResult) {
                Text(
                    "Over the last 2 weeks, how often have you been bothered by any of the following problems?",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                questions.forEachIndexed { index, question ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${index + 1}. $question", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val options = listOf("Not at all", "Several days", "More than half", "Nearly every day")
                            options.forEachIndexed { score, label ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        viewModel.setAnswer(index, score)
                                    }.padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = viewModel.answers[index] == score,
                                        onClick = { viewModel.setAnswer(index, score) }
                                    )
                                    Text(label, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (viewModel.answers.size == questions.size) {
                            finalScore = viewModel.calculateScore()
                            showResult = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = viewModel.answers.size == questions.size
                ) {
                    Text("Submit & View Evaluation")
                }
                if (viewModel.answers.size < questions.size) {
                    Text(
                        "Please answer all questions (${viewModel.answers.size}/${questions.size})",
                        fontSize = 11.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                val (title, description) = viewModel.getSeverity(finalScore)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Your Score: $finalScore / 27", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(description, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (finalScore >= 15) {
                    Button(
                        onClick = onNavigateToSOS,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Connect with Helpline (SOS)", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        viewModel.reset()
                        showResult = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Retake Test")
                }
            }
        }
    }
}
