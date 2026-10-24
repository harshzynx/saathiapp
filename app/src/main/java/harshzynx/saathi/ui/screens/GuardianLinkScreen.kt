package harshzynx.saathi.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardianLinkScreen(
    onBackClick: () -> Unit,
    viewModel: GuardianViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    if (viewModel.showInvitePrompt) {
        AlertDialog(
            onDismissRequest = { viewModel.showInvitePrompt = false },
            title = { Text("Inform your Saathi? 🤝") },
            text = { 
                Text("You've linked ${viewModel.guardianName}! Should we send them a quick intro message so they know how Saathi works?") 
            },
            confirmButton = {
                Button(onClick = { 
                    sendGuardianIntro(context, viewModel.guardianName, viewModel.guardianEmail)
                    viewModel.showInvitePrompt = false
                }) {
                    Text("Yes, send Intro")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showInvitePrompt = false }) {
                    Text("Not now")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trusted Person / Guardian Link 🛡️") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "3-Level Safety & Support",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Link a trusted parent, guardian, or mentor. They will only receive neutral well-being alerts if persistent safety risks are detected (Pre-consented).",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (viewModel.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Trusted Contact Details", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = viewModel.guardianName,
                        onValueChange = { viewModel.guardianName = it },
                        label = { Text("Contact Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isLinked && !viewModel.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = viewModel.guardianEmail,
                        onValueChange = { viewModel.guardianEmail = it },
                        label = { Text("Contact Email or Mobile Number") },
                        placeholder = { Text("e.g. parent@email.com or 9876543210") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isLinked && !viewModel.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = viewModel.safetyAlertConsent,
                            onCheckedChange = { viewModel.safetyAlertConsent = it },
                            enabled = !viewModel.isLinked && !viewModel.isLoading
                        )
                        Text(
                            text = "Allow sending a neutral safety alert to this contact if persistent severe low mood or risk is detected.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val isValidInput = viewModel.guardianName.isNotBlank() && 
                            (viewModel.guardianEmail.contains("@") || viewModel.guardianEmail.filter { it.isDigit() }.length >= 6)

                    if (viewModel.isLinked) {
                        Button(
                            onClick = { 
                                viewModel.updateGuardianLink(false) {
                                    Toast.makeText(context, "Trusted Contact Unlinked", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            enabled = !viewModel.isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.LinkOff, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Unlink Contact", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { 
                                viewModel.updateGuardianLink(true) {
                                    Toast.makeText(context, "Trusted Contact Linked Successfully!", Toast.LENGTH_SHORT).show()
                                    viewModel.showInvitePrompt = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = isValidInput && !viewModel.isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Link Trusted Contact ✨", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy Assurance & Zero Diagnosis Guarantee
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Privacy Guarantee: Your private journal entries and chats are NEVER shared. Alerts never show clinical diagnosis—they only notify your trusted contact to check in gently.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

fun sendGuardianIntro(context: Context, guardianName: String, contactDetail: String) {
    if (contactDetail.contains("@")) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(contactDetail))
            putExtra(Intent.EXTRA_SUBJECT, "I've chosen you as my 'Saathi' Support 🤝")
            putExtra(Intent.EXTRA_TEXT, "Hi $guardianName,\n\nI've started using the Saathi Wellbeing App to track my mood and study goals. I've chosen you as my trusted companion ('Saathi').\n\nYou might receive a short alert if I'm feeling low and need a chat. Sent with love from Saathi App.")
        }
        context.startActivity(Intent.createChooser(intent, "Send Intro Email"))
    } else {
        val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$contactDetail")
            putExtra("sms_body", "Hi $guardianName, I've chosen you as my trusted guardian in the Saathi Wellbeing App 🤝. Thank you for being there for me!")
        }
        context.startActivity(Intent.createChooser(smsIntent, "Send Intro SMS"))
    }
}
