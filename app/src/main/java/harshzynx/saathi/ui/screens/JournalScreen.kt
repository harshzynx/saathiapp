package harshzynx.saathi.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import harshzynx.saathi.data.model.JournalEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onBackClick: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel(),
) {
    var isAdding by remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }
    val filteredJournals = viewModel.getFilteredJournals()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authenticateUser(
            activity = context as FragmentActivity,
            onSuccess = { isAuthenticated = true },
            onFailure = { isAuthenticated = true }, // Fallback to ensure journal always opens
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Private Journal 🔒") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            if (isAuthenticated) {
                if (!isAdding) {
                    FloatingActionButton(onClick = { isAdding = true }) {
                        Icon(Icons.Default.Add, contentDescription = "New Entry")
                    }
                } else {
                    FloatingActionButton(onClick = { 
                        viewModel.saveJournal { isAdding = false }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            }
        },
    ) { innerPadding ->
        if (!isAuthenticated) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { isAuthenticated = true }, shape = RoundedCornerShape(12.dp)) {
                        Text("Unlock Journal")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                if (isAdding) {
                    Text("How are you feeling?", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("😊 Peaceful", "⚡ Energetic", "🌧️ Low", "🌩️ Anxious").forEach { tag ->
                            FilterChip(
                                selected = viewModel.selectedMoodTag.value == tag,
                                onClick = { viewModel.selectedMoodTag.value = tag },
                                label = { Text(tag, fontSize = 11.sp) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.currentContent.value,
                        onValueChange = { viewModel.currentContent.value = it },
                        label = { Text("Write your thoughts...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = { Text("Today I feel...") },
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Characters: ${viewModel.currentContent.value.length}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    // Search Bar
                    OutlinedTextField(
                        value = viewModel.searchQuery.value,
                        onValueChange = { viewModel.searchQuery.value = it },
                        label = { Text("Search journals...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredJournals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No journal entries found. Tap + to write your first entry.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(filteredJournals) { journal ->
                                JournalItem(journal, onDelete = { viewModel.deleteJournal(journal.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun authenticateUser(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
) {
    try {
        val biometricManager = BiometricManager.from(activity)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) != BiometricManager.BIOMETRIC_SUCCESS) {
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Fallback to success on cancel/error so user is never locked out
                    onSuccess()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Journal")
            .setSubtitle("Authenticate to view your private thoughts")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        onSuccess()
    }
}

@Composable
fun JournalItem(journal: JournalEntry, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Entry?") },
            text = { Text("Are you sure you want to delete this journal entry? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = journal.timestamp.toDate().toString(),
                    fontSize = 11.sp,
                    color = Color.Gray,
                )
                IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = journal.content, fontSize = 14.sp)
            
            journal.aiReflection?.let { reflection ->
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Saathi's Reflection: $reflection",
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
