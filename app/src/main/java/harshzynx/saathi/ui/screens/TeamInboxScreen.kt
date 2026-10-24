package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.google.firebase.auth.FirebaseAuth
import harshzynx.saathi.data.model.SupportMessage
import harshzynx.saathi.data.model.SupportTicket
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInboxScreen(
    onLogout: () -> Unit,
    viewModel: TeamInboxViewModel = hiltViewModel()
) {
    val tickets = viewModel.tickets
    val selectedTicketId = viewModel.selectedTicketId
    val messages = viewModel.selectedTicketMessages

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Support Inbox 👨‍🏫") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Log Out", color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE3F2FD), GradientEnd)))
        ) {
            // Left / Main Ticket List
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp)
            ) {
                Text("Incoming Student Tickets", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))

                if (tickets.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No pending student requests.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tickets) { ticket ->
                            TicketItemCard(
                                ticket = ticket,
                                isSelected = selectedTicketId == ticket.id,
                                onClick = { viewModel.selectTicket(ticket.id) }
                            )
                        }
                    }
                }
            }

            // Right Chat View for Selected Ticket
            if (selectedTicketId != null) {
                Card(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Active Support Conversation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(messages) { msg ->
                                CounselorChatBubble(msg)
                            }
                        }

                        Surface(
                            tonalElevation = 4.dp,
                            color = Color(0xFFF8FAFC)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = viewModel.teamReplyInput,
                                    onValueChange = { viewModel.teamReplyInput = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Reply as Counselor/Team...") },
                                    shape = RoundedCornerShape(20.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                FloatingActionButton(
                                    onClick = { viewModel.sendTeamReply() },
                                    modifier = Modifier.size(44.dp),
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select a student ticket to start chatting", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun TicketItemCard(ticket: SupportTicket, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(ticket.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (ticket.status == "pending") Color(0xFFFFE0B2) else Color(0xFFC8E6C9)
                ) {
                    Text(
                        ticket.status.replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("ID: ${ticket.id.take(6)}...", fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CounselorChatBubble(message: SupportMessage) {
    val currentStaffId = FirebaseAuth.getInstance().currentUser?.uid ?: "counselor_1"
    
    // For staff inbox: staff's own reply is on RIGHT, student's message is on LEFT
    val isMyStaffReply = message.senderId == currentStaffId || message.senderId == "counselor_1" || message.senderName.contains("Counselor", true) || message.senderName.contains("Support", true)
    
    val alignment = if (isMyStaffReply) Alignment.End else Alignment.Start
    val containerColor = if (isMyStaffReply) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9)
    val contentColor = if (isMyStaffReply) Color.White else Color(0xFF1E293B)
    val shape = if (isMyStaffReply) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Text(if (isMyStaffReply) "You (Staff)" else message.senderName, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 6.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Surface(
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            shadowElevation = 1.dp
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}
