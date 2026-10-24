package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
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
import com.google.firebase.auth.FirebaseAuth
import harshzynx.saathi.data.model.SupportMessage
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamChatScreen(
    memberName: String,
    onBackClick: () -> Unit,
    viewModel: TeamChatViewModel = hiltViewModel()
) {
    val messages = viewModel.messages
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(memberName) {
        viewModel.initializeChat(memberName)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report or Block?") },
            text = { Text("Are you sure you want to report this support interaction?") },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        Toast.makeText(context, "Support interaction reported.", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Report & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(memberName, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("Saathi Support Volunteer • Online 🟢", fontSize = 11.sp, color = Color(0xFF00796B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Calling helpline...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Report") },
                            onClick = {
                                showMenu = false
                                showReportDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("End Chat") },
                            onClick = {
                                showMenu = false
                                onBackClick()
                            }
                        )
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
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { msg ->
                    RealTeamChatBubble(msg, staffMemberName = memberName)
                }
            }

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
                    TextField(
                        value = viewModel.inputMessage,
                        onValueChange = { viewModel.inputMessage = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Apni baat share kijiye...") },
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
                            viewModel.sendMessage(memberName)
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
fun RealTeamChatBubble(message: SupportMessage, staffMemberName: String) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Check if message is sent by Staff/Counselor
    val isStaffMessage = message.senderId == "staff" || 
            message.senderId == "counselor_1" || 
            message.senderName.equals(staffMemberName, ignoreCase = true) ||
            message.senderName.contains("Counselor", ignoreCase = true) || 
            message.senderName.contains("Support", ignoreCase = true) || 
            message.senderName.contains("Volunteer", ignoreCase = true)

    // User's own message is on the RIGHT side; Staff message is on the LEFT side (WhatsApp style)
    val isMyMessage = !isStaffMessage && (
        (!currentUserId.isNullOrBlank() && message.senderId == currentUserId) || 
        message.senderName == "Student" || 
        message.senderName == "You"
    )

    val alignment = if (isMyMessage) Alignment.End else Alignment.Start
    val containerColor = if (isMyMessage) MaterialTheme.colorScheme.primary else Color.White
    val contentColor = if (isMyMessage) Color.White else Color(0xFF1E293B)
    val shape = if (isMyMessage) {
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
        Text(
            text = if (isMyMessage) "You" else message.senderName,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isMyMessage) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.padding(horizontal = 6.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
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
            }
        }
    }
}
