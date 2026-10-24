package harshzynx.saathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
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
import harshzynx.saathi.data.model.SupportRoom
import harshzynx.saathi.data.model.TeamMember
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSupportHubScreen(
    onBackClick: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToRoom: (String) -> Unit,
    viewModel: TeamSupportHubViewModel = hiltViewModel()
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("Chats") } // Chats, Rooms

    val teamMembers = viewModel.teamMembers
    val supportRooms = viewModel.supportRooms

    val filteredMembers = teamMembers.filter { member ->
        when (selectedFilter) {
            "Women" -> member.gender == "Female"
            "Men" -> member.gender == "Male"
            "Available Now" -> member.isAvailable
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Team Support 👥") },
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE3F2FD), GradientEnd)))
                .padding(16.dp)
        ) {
            // Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Need someone to talk to?", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Our real support team members are here to listen, support and help you feel better. 💙", fontSize = 12.sp, color = Color.DarkGray, lineHeight = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector: Chats vs Rooms
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedCategory = "Chats" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == "Chats") MaterialTheme.colorScheme.primary else Color.White,
                        contentColor = if (selectedCategory == "Chats") Color.White else Color.DarkGray
                    )
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Private Chat")
                }

                Button(
                    onClick = { selectedCategory = "Rooms" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedCategory == "Rooms") MaterialTheme.colorScheme.primary else Color.White,
                        contentColor = if (selectedCategory == "Rooms") Color.White else Color.DarkGray
                    )
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Support Rooms")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedCategory == "Chats") {
                // Filters: All, Women, Men, Available Now
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Women", "Men", "Available Now").forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredMembers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No team members online right now.", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("You can use AI Companion or SOS in the meantime.", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredMembers) { member ->
                            TeamMemberCard(member, onStartChat = { onNavigateToChat(member.name) })
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(supportRooms) { room ->
                        SupportRoomCard(room, onJoin = { onNavigateToRoom(room.title) })
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberCard(member: TeamMember, onStartChat: () -> Unit) {
    Card(
        onClick = onStartChat,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(member.name.take(1), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(member.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (member.isAvailable) Color.Green else Color.Gray, RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (member.isAvailable) "Online" else "Offline", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(member.bio, fontSize = 12.sp, color = Color.DarkGray, maxLines = 1)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    member.tags.take(2).forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        ) {
                            Text(tag, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun SupportRoomCard(room: SupportRoom, onJoin: () -> Unit) {
    Card(
        onClick = onJoin,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(room.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${room.activeCount} members active • ${room.category}", fontSize = 12.sp, color = Color.Gray)
            }
            Button(onClick = onJoin, shape = RoundedCornerShape(12.dp)) {
                Text("Join Room")
            }
        }
    }
}
