package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import harshzynx.saathi.data.model.*
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTeamManagementScreen(
    onNavigateToInbox: () -> Unit,
    onLogout: () -> Unit,
    viewModel: AdminTeamManagementViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddStaffDialog by remember { mutableStateOf(false) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }
    var selectedUserForSuspend by remember { mutableStateOf<User?>(null) }
    var suspendReasonInput by remember { mutableStateOf("Violation of Community Guidelines") }

    val context = LocalContext.current

    // Security Authorization Guard
    if (viewModel.isCheckingAuth) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!viewModel.isAuthorizedAdmin) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Access Denied", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        viewModel.errorMessage.value ?: "Only authorized Super Admins can access this Control Panel.",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onLogout, shape = RoundedCornerShape(12.dp)) {
                        Text("Log Out")
                    }
                }
            }
        }
        return
    }

    // Add Staff Dialog
    if (showAddStaffDialog) {
        AlertDialog(
            onDismissRequest = { showAddStaffDialog = false },
            title = { Text("Add Team Member / Counselor 👨‍🏫", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = viewModel.newMemberName.value,
                        onValueChange = { viewModel.newMemberName.value = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.newMemberEmail.value,
                        onValueChange = { viewModel.newMemberEmail.value = it },
                        label = { Text("Email (e.g. name@saathi.team)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.newMemberRole.value,
                        onValueChange = { viewModel.newMemberRole.value = it },
                        label = { Text("Role (Senior Counselor, Peer Volunteer)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.newMemberTimings.value,
                        onValueChange = { viewModel.newMemberTimings.value = it },
                        label = { Text("Support Timings (e.g. 9 AM - 6 PM)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTeamMember {
                            showAddStaffDialog = false
                            Toast.makeText(context, "Staff member added successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Staff")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStaffDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Suspend User Dialog
    if (selectedUserForSuspend != null) {
        val user = selectedUserForSuspend!!
        AlertDialog(
            onDismissRequest = { selectedUserForSuspend = null },
            title = { Text(if (user.accountStatus == "SUSPENDED") "Reactivate User?" else "Suspend User Account?") },
            text = {
                Column {
                    Text("User: ${user.name} (${user.email})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = suspendReasonInput,
                        onValueChange = { suspendReasonInput = it },
                        label = { Text("Reason for Action") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleUserSuspendStatus(user, suspendReasonInput)
                        selectedUserForSuspend = null
                        Toast.makeText(context, "User status updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.accountStatus == "SUSPENDED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (user.accountStatus == "SUSPENDED") "Reactivate" else "Suspend")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForSuspend = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Send Broadcast Announcement Dialog
    if (showAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showAnnouncementDialog = false },
            title = { Text("Send Broadcast Announcement 📢", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = viewModel.announcementTitle.value,
                        onValueChange = { viewModel.announcementTitle.value = it },
                        label = { Text("Announcement Title") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewModel.announcementBody.value,
                        onValueChange = { viewModel.announcementBody.value = it },
                        label = { Text("Message Body") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendAnnouncement {
                            showAnnouncementDialog = false
                            Toast.makeText(context, "Announcement broadcast sent!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Broadcast")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnouncementDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    val tabs = listOf("📊 Overview", "👥 Staff", "🎓 Users", "💬 Support", "🤖 AI Config", "🚨 Emergency", "💬 Feedback", "📢 Announce", "🛡️ Security", "📜 Audit")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Super Admin Panel 👑", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToInbox) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Support Inbox", tint = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
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
            // Scrollable Admin Module Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> AdminOverviewModule(
                        totalUsers = viewModel.totalUserCount,
                        activeStaff = viewModel.activeStaffCount,
                        pendingTickets = viewModel.pendingTicketsCount,
                        databaseHealth = viewModel.databaseHealth,
                        aiHealth = viewModel.aiHealthStatus,
                        monthlyCost = viewModel.monthlyEstimatedCostUsd,
                        onNavigateToInbox = onNavigateToInbox
                    )
                    1 -> StaffManagementModule(
                        staffList = viewModel.teamMembers,
                        onAddClick = { showAddStaffDialog = true },
                        onToggleStatus = { viewModel.toggleStaffStatus(it) },
                        onDelete = { viewModel.deleteTeamMember(it.id, it.name) }
                    )
                    2 -> UserGovernanceModule(
                        userList = viewModel.userList,
                        searchQuery = viewModel.searchQuery,
                        onSearchChange = { viewModel.searchQuery = it },
                        onSuspendClick = { user ->
                            selectedUserForSuspend = user
                            suspendReasonInput = if (user.accountStatus == "SUSPENDED") "Reactivating Account" else "Violation of Guidelines"
                        },
                        onDeleteClick = { user ->
                            viewModel.deleteUserAccount(user.uid, user.email)
                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                        }
                    )
                    3 -> SupportManagementModule(
                        tickets = viewModel.supportTickets,
                        staffList = viewModel.teamMembers,
                        onReassign = { ticketId, staffName, staffEmail ->
                            viewModel.reassignTicket(ticketId, staffName, staffEmail)
                            Toast.makeText(context, "Ticket assigned to $staffName", Toast.LENGTH_SHORT).show()
                        },
                        onUpdateStatus = { ticketId, status ->
                            viewModel.updateTicketStatus(ticketId, status)
                        }
                    )
                    4 -> AiAndAppConfigModule(
                        config = viewModel.appConfig,
                        history = viewModel.configHistoryList,
                        onSaveConfig = { newConfig ->
                            viewModel.saveAppConfig(newConfig)
                            Toast.makeText(context, "Configuration saved!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    5 -> EmergencyAndTestingModule(
                        maintenanceMode = viewModel.appConfig.maintenanceMode,
                        onToggleMaintenance = {
                            viewModel.toggleEmergencyMaintenanceMode()
                            Toast.makeText(context, "Maintenance Mode Toggled!", Toast.LENGTH_SHORT).show()
                        },
                        onTestGroq = {
                            viewModel.testGroqConnection { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                    6 -> FeedbackManagementModule(
                        feedbackList = viewModel.feedbackList,
                        onUpdateStatus = { id, status ->
                            viewModel.updateFeedbackStatus(id, status)
                            Toast.makeText(context, "Feedback status updated to $status", Toast.LENGTH_SHORT).show()
                        }
                    )
                    7 -> AnnouncementsModule(
                        announcements = viewModel.announcements,
                        onNewAnnouncementClick = { showAnnouncementDialog = true }
                    )
                    8 -> SecurityCenterModule(
                        alerts = viewModel.securityAlertsList
                    )
                    9 -> AuditLogModule(
                        logs = viewModel.auditLogs
                    )
                }
            }
        }
    }
}

// 1. OVERVIEW MODULE
@Composable
fun AdminOverviewModule(
    totalUsers: Int,
    activeStaff: Int,
    pendingTickets: Int,
    databaseHealth: String,
    aiHealth: String,
    monthlyCost: String,
    onNavigateToInbox: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            onClick = onNavigateToInbox,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Live Support Desk 💬", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Reply to student chat & call requests", fontSize = 12.sp, color = Color.DarkGray)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Owner Quick Controls Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Owner Quick Controls ⚙️", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Database: $databaseHealth", fontSize = 12.sp, color = Color.DarkGray)
                    Text("AI: $aiHealth", fontSize = 12.sp, color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cost: $monthlyCost", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Latency: 42ms", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Total Users 🎓", "$totalUsers", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            MetricCard("Active Staff 👨‍🏫", "$activeStaff", Color(0xFF00796B), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Pending Tickets 💬", "$pendingTickets", Color(0xFFF57C00), Modifier.weight(1f))
            MetricCard("AI Engine Health", aiHealth, Color(0xFF7E57C2), Modifier.weight(1f))
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, accentColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
        }
    }
}

// 2. STAFF MANAGEMENT MODULE
@Composable
fun StaffManagementModule(
    staffList: List<AdminTeamMember>,
    onAddClick: () -> Unit,
    onToggleStatus: (AdminTeamMember) -> Unit,
    onDelete: (AdminTeamMember) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Authorized Staff (${staffList.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(onClick = onAddClick, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Staff")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (staffList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No staff added yet. Tap Add Staff.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(staffList) { staff ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFE0F2F1), modifier = Modifier.size(44.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00796B))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(staff.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${staff.email} • ${staff.role}", fontSize = 11.sp, color = Color.Gray)
                                Text("Timings: ${staff.supportTimings} • Status: ${staff.status}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            TextButton(onClick = { onToggleStatus(staff) }) {
                                Text(if (staff.status == "ACTIVE") "Deactivate" else "Activate", fontSize = 11.sp)
                            }
                            IconButton(onClick = { onDelete(staff) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. USER GOVERNANCE MODULE
@Composable
fun UserGovernanceModule(
    userList: List<User>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSuspendClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit
) {
    val filteredUsers = userList.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text("Search users by name or email...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name.ifBlank { "Saathi Student" }, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${user.email} • ${user.institutionName ?: "No College"}", fontSize = 11.sp, color = Color.Gray)
                            Text("Status: ${user.accountStatus}", fontSize = 10.sp, color = if (user.accountStatus == "SUSPENDED") MaterialTheme.colorScheme.error else Color(0xFF00796B), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { onSuspendClick(user) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.accountStatus == "SUSPENDED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (user.accountStatus == "SUSPENDED") "Reactivate" else "Suspend", fontSize = 11.sp)
                        }
                        IconButton(onClick = { onDeleteClick(user) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// 4. SUPPORT TICKET MANAGEMENT MODULE
@Composable
fun SupportManagementModule(
    tickets: List<SupportTicket>,
    staffList: List<AdminTeamMember>,
    onReassign: (String, String, String) -> Unit,
    onUpdateStatus: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Active Support Requests (${tickets.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (tickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No support tickets generated yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tickets) { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ticket #${ticket.id.take(8)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Status: ${ticket.status}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Student: ${ticket.userName}", fontSize = 12.sp, color = Color.DarkGray)
                            Text("Assigned: ${ticket.assignedStaff.ifBlank { "Unassigned" }}", fontSize = 11.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (staffList.isNotEmpty()) {
                                    val staff = staffList.first()
                                    OutlinedButton(onClick = { onReassign(ticket.id, staff.name, staff.email) }, shape = RoundedCornerShape(10.dp)) {
                                        Text("Assign ${staff.name}", fontSize = 11.sp)
                                    }
                                }
                                Button(onClick = { onUpdateStatus(ticket.id, "RESOLVED") }, shape = RoundedCornerShape(10.dp)) {
                                    Text("Mark Resolved", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. AI & APP CONFIGURATION MODULE (WITH CONFIG HISTORY)
@Composable
fun AiAndAppConfigModule(
    config: AppConfig,
    history: List<ConfigHistory>,
    onSaveConfig: (AppConfig) -> Unit
) {
    var aiEnabled by remember(config) { mutableStateOf(config.aiEnabled) }
    var selectedModel by remember(config) { mutableStateOf(config.selectedAiModel) }
    var humanSupportEnabled by remember(config) { mutableStateOf(config.humanSupportEnabled) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("AI Companion Settings 🤖", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable AI Chat Globally", fontSize = 14.sp)
                    Switch(checked = aiEnabled, onCheckedChange = { aiEnabled = it })
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Primary AI Model", fontSize = 12.sp, color = Color.Gray)
                listOf("llama-3.3-70b-versatile", "llama-3.1-8b-instant").forEach { model ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedModel = model }) {
                        RadioButton(selected = selectedModel == model, onClick = { selectedModel = model })
                        Text(model, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Human Support Chat", fontSize = 14.sp)
                    Switch(checked = humanSupportEnabled, onCheckedChange = { humanSupportEnabled = it })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onSaveConfig(config.copy(aiEnabled = aiEnabled, selectedAiModel = selectedModel, humanSupportEnabled = humanSupportEnabled))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save AI & App Config")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Config History
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Configuration History & Rollback 📜", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))

                if (history.isEmpty()) {
                    Text("No configuration changes recorded yet.", fontSize = 12.sp, color = Color.Gray)
                } else {
                    history.take(5).forEach { item ->
                        Text("v${item.version} • Changed by: ${item.changedBy.take(15)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(item.newConfigSummary, fontSize = 10.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

// 6. EMERGENCY & TESTING MODULE
@Composable
fun EmergencyAndTestingModule(
    maintenanceMode: Boolean,
    onToggleMaintenance: () -> Unit,
    onTestGroq: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Emergency Operations & Controls 🚨", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Global Maintenance Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Puts non-essential features on pause during system upgrades.", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = maintenanceMode, onCheckedChange = { onToggleMaintenance() })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Diagnostics & Test Sandbox 🛠️", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onTestGroq,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Groq AI Live Connection")
                }
            }
        }
    }
}

// 7. FEEDBACK MANAGEMENT MODULE
@Composable
fun FeedbackManagementModule(
    feedbackList: List<FeedbackItem>,
    onUpdateStatus: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("User Bug Reports & Suggestions (${feedbackList.size}) 💬", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (feedbackList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No user feedback submitted yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(feedbackList) { item ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.category, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text("Status: ${item.status}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(item.message, fontSize = 12.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onUpdateStatus(item.id, "IN_REVIEW") }, shape = RoundedCornerShape(8.dp)) {
                                    Text("In Review", fontSize = 10.sp)
                                }
                                Button(onClick = { onUpdateStatus(item.id, "RESOLVED") }, shape = RoundedCornerShape(8.dp)) {
                                    Text("Resolve", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 8. ANNOUNCEMENTS MODULE
@Composable
fun AnnouncementsModule(
    announcements: List<Announcement>,
    onNewAnnouncementClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Broadcast Notifications 📢", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Button(onClick = onNewAnnouncementClick, shape = RoundedCornerShape(12.dp)) {
                Text("+ New Broadcast")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No announcements sent yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(announcements) { ann ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(ann.body, fontSize = 13.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Audience: ${ann.targetGroup} • Sent by: ${ann.authorEmail}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// 9. SECURITY CENTER MODULE
@Composable
fun SecurityCenterModule(alerts: List<SecurityAlert>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Security Center & Audit Alerts 🛡️", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No security alerts or role tamper attempts recorded.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(alerts) { alert ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(alert.alertType, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                                Text("Severity: ${alert.severity}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("User: ${alert.userEmail} • ${alert.details}", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

// 10. AUDIT LOG MODULE
@Composable
fun AuditLogModule(logs: List<AdminAuditLog>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Super Admin Audit Trail 📜", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No administrative audit logs recorded yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs) { log ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                Text(log.actorEmail, fontSize = 11.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
