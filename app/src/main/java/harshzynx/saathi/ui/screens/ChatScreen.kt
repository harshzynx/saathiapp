package harshzynx.saathi.ui.screens

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.util.Locale

@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    ChatContent(
        messages = viewModel.messages,
        isTyping = viewModel.isTyping,
        isJustListen = viewModel.isJustListen,
        onToggleJustListen = { viewModel.isJustListen = it },
        selectedCategory = viewModel.selectedCategory,
        onSelectCategory = { viewModel.selectedCategory = it },
        onBackClick = onBackClick,
        onSendMessage = { viewModel.sendMessage(it) },
        onClearHistory = { viewModel.clearHistory() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatContent(
    messages: List<Message>,
    isTyping: Boolean,
    isJustListen: Boolean,
    onToggleJustListen: (Boolean) -> Unit,
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var speechLang by remember { mutableStateOf("hi-IN") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val context = LocalContext.current
    
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var lastSpokenText by remember { mutableStateOf("") }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear Chat History? 🧹", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to erase all chat messages in this conversation?") },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearHistory()
                        Toast.makeText(context, "Chat History Cleared 🧹", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("hi", "IN")
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                inputText = results[0]
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLang)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Saathi sun raha hai... boliye 🎙️")
            }
            try {
                speechRecognizerLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
            val lastMsg = messages.last()
            // Speak only once per distinct AI message
            if (!lastMsg.isUser && lastMsg.text != "..." && lastMsg.text != lastSpokenText) {
                lastSpokenText = lastMsg.text
                tts?.speak(lastMsg.text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Saathi AI Friend 🫂", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(if (isJustListen) "Mode: Just Listening (No Advice)" else "Voice & Chat Ready ✨", fontSize = 11.sp, color = if (isJustListen) MaterialTheme.colorScheme.secondary else Color(0xFF00796B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showClearConfirmDialog = true }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear History", tint = MaterialTheme.colorScheme.error)
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        TextButton(onClick = {
                            speechLang = if (speechLang == "hi-IN") "en-IN" else "hi-IN"
                            tts?.language = if (speechLang == "hi-IN") Locale("hi", "IN") else Locale("en", "US")
                            Toast.makeText(context, "Voice Language: $speechLang", Toast.LENGTH_SHORT).show()
                        }) {
                            Text(if (speechLang == "hi-IN") "🇮🇳 HI" else "🇬🇧 EN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE3F2FD), GradientEnd)))
        ) {
            // Category Selector & Just Listen Toggle
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                color = Color.White,
                shape = RoundedCornerShape(0.dp, 0.dp, 24.dp, 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("आज मुझे सिर्फ सुनने वाला चाहिए (Just Listen)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Switch(
                            checked = isJustListen,
                            onCheckedChange = onToggleJustListen,
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf("Exams", "Hostel", "Loneliness", "Career", "Relationship", "Family")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) },
                                label = { Text(cat, fontSize = 11.sp) },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message, onSpeak = { text ->
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    })
                }
                if (isTyping) {
                    item {
                        ChatBubble(Message(text = "...", isUser = false), onSpeak = {})
                    }
                }
            }

            // Input Area with Microphone & Send
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White,
                shape = RoundedCornerShape(24.dp, 24.dp, 0.dp, 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Microphone Voice Input Button
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        IconButton(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Bol kar ya type karke likhiye...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F3F4),
                            unfocusedContainerColor = Color(0xFFF1F3F4),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, onSpeak: (String) -> Unit) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val containerColor = if (isUser) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isUser) Color.White else Color.DarkGray
    val shape = if (isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                )
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSpeak(message.text) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
