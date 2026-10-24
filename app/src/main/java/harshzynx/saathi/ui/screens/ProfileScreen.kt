package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToGuardianLink: () -> Unit,
    onNavigateToParentalGuidance: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (viewModel.saveMessage != null) {
        LaunchedEffect(viewModel.saveMessage) {
            Toast.makeText(context, viewModel.saveMessage, Toast.LENGTH_SHORT).show()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account & Data?") },
            text = { Text("This action is permanent and will delete all your journals, check-ins, and profile data.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteAccount(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Forever")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings ⚙️") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveProfile {
                                Toast.makeText(context, "Profile Saved Successfully! ✨", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    viewModel.saveProfile {
                        Toast.makeText(context, "Profile Saved Successfully! ✨", Toast.LENGTH_SHORT).show()
                    }
                },
                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
                text = { Text("Save Profile ✨", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Your Account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = {},
                enabled = false,
                label = { Text("Email (Read-only)") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Education & Institution", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                listOf("School", "College", "University").forEach { level ->
                    FilterChip(
                        selected = viewModel.educationLevel == level,
                        onClick = { viewModel.educationLevel = level },
                        label = { Text(level) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.institutionName,
                onValueChange = { viewModel.institutionName = it },
                label = { Text("College / Institution Name") },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.collegeEmail,
                onValueChange = { viewModel.collegeEmail = it },
                label = { Text("College Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.studentMobile,
                onValueChange = { viewModel.studentMobile = it },
                label = { Text("Student Mobile No.") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Trusted Circle & Emergency Contacts", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.parentMobile,
                onValueChange = { viewModel.parentMobile = it },
                label = { Text("Parent / Guardian Mobile No.") },
                leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.teacherEmail,
                onValueChange = { viewModel.teacherEmail = it },
                label = { Text("Teacher / Mentor Email") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.friendMobile,
                onValueChange = { viewModel.friendMobile = it },
                label = { Text("Trusted Friend Mobile No.") },
                leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("AI Companion Configuration", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Enter your custom Groq API key to power live cloud AI responses.", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.groqApiKey,
                onValueChange = { viewModel.groqApiKey = it },
                label = { Text("Groq API Key (gsk_...)") },
                leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Prominent Save Changes Button inside main form
            Button(
                onClick = {
                    viewModel.saveProfile {
                        Toast.makeText(context, "Profile Saved Successfully! ✨", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Profile Changes ✨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Guardian & Safety", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onNavigateToGuardianLink,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Link Guardian for Safety")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onNavigateToParentalGuidance,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.FamilyRestroom, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Parental Guidance Tips")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Session & Privacy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Account & All Data")
            }

            // Extra bottom padding so FloatingActionButton never overlaps content
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
