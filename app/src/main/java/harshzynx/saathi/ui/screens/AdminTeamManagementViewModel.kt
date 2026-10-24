package harshzynx.saathi.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.*
import harshzynx.saathi.domain.GroqService
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminTeamManagementViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val groqService: GroqService
) : ViewModel() {

    private val TAG = "AdminViewModel"

    // Admin Verification State
    var isAuthorizedAdmin by mutableStateOf(false)
        private set

    var isCheckingAuth by mutableStateOf(true)
        private set

    // Dashboard Overview & Diagnostics
    var totalUserCount by mutableStateOf(0)
    var activeStaffCount by mutableStateOf(0)
    var pendingTicketsCount by mutableStateOf(0)
    var databaseHealth by mutableStateOf("Healthy 🟢")
    var aiHealthStatus by mutableStateOf("100% Active 🟢")
    var apiLatencyMs by mutableStateOf("42ms")

    // Cost & Usage Monitoring Estimates
    var monthlyEstimatedCostUsd by mutableStateOf("$2.40 / $10.00 Budget")
    var totalEstimatedTokens by mutableStateOf("124,500 Tokens")

    // Module State Lists
    val teamMembers = mutableStateListOf<AdminTeamMember>()
    val userList = mutableStateListOf<User>()
    val supportTickets = mutableStateListOf<SupportTicket>()
    val abuseReports = mutableStateListOf<AbuseReport>()
    val auditLogs = mutableStateListOf<AdminAuditLog>()
    val announcements = mutableStateListOf<Announcement>()
    val configHistoryList = mutableStateListOf<ConfigHistory>()
    val feedbackList = mutableStateListOf<FeedbackItem>()
    val securityAlertsList = mutableStateListOf<SecurityAlert>()

    // Form / Search Inputs
    var searchQuery by mutableStateOf("")
    var newMemberName = mutableStateOf("")
    var newMemberEmail = mutableStateOf("")
    var newMemberRole = mutableStateOf("Counselor")
    var newMemberGender = mutableStateOf("All")
    var newMemberTimings = mutableStateOf("9:00 AM - 6:00 PM")

    var announcementTitle = mutableStateOf("")
    var announcementBody = mutableStateOf("")
    var announcementTarget = mutableStateOf("ALL")

    var appConfig by mutableStateOf(AppConfig())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var successMessage = mutableStateOf<String?>(null)

    val currentAdminEmail: String get() = auth.currentUser?.email ?: "harshsharma18089@gmail.com"

    init {
        verifyAdminAccess()
    }

    fun verifyAdminAccess() {
        isCheckingAuth = true
        val currentUser = auth.currentUser
        val email = currentUser?.email?.trim()?.lowercase() ?: ""

        viewModelScope.launch {
            try {
                // Owner email or Firestore role == "OWNER" / "ADMIN"
                var isOwner = email == "harshsharma18089@gmail.com"
                if (!isOwner && currentUser != null) {
                    val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
                    val role = userDoc.getString("role") ?: ""
                    if (role.equals("OWNER", ignoreCase = true) || role.equals("ADMIN", ignoreCase = true)) {
                        isOwner = true
                    }
                }

                isAuthorizedAdmin = isOwner
                if (isOwner) {
                    loadAdminData()
                } else {
                    errorMessage.value = "Unauthorized: Only authorized Super Admins can access this Control Panel."
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error verifying admin access", e)
                isAuthorizedAdmin = false
                errorMessage.value = "Failed to verify admin security credentials: ${e.localizedMessage}"
            } finally {
                isCheckingAuth = false
            }
        }
    }

    private fun loadAdminData() {
        fetchDashboardMetrics()
        fetchTeamMembers()
        fetchUsers()
        fetchSupportTickets()
        fetchAppConfig()
        fetchAbuseReports()
        fetchAnnouncements()
        fetchAuditLogs()
        fetchConfigHistory()
        fetchFeedbackItems()
        fetchSecurityAlerts()
    }

    // 1. Dashboard Metrics
    private fun fetchDashboardMetrics() {
        firestore.collection("users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                totalUserCount = snapshot.size()
            }
        }

        firestore.collection("team_staff").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                activeStaffCount = snapshot.documents.count { it.getString("status") != "INACTIVE" }
            }
        }

        firestore.collection("support_tickets").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                pendingTicketsCount = snapshot.documents.count { it.getString("status")?.uppercase() == "OPEN" || it.getString("status")?.uppercase() == "PENDING" }
            }
        }
    }

    // 2. Staff Management
    private fun fetchTeamMembers() {
        firestore.collection("team_staff")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(AdminTeamMember::class.java)
                    teamMembers.clear()
                    teamMembers.addAll(list)
                }
            }
    }

    fun addTeamMember(onSuccess: () -> Unit) {
        val name = newMemberName.value.trim()
        val email = newMemberEmail.value.trim().lowercase()
        val role = newMemberRole.value.trim()
        val gender = newMemberGender.value.trim()
        val timings = newMemberTimings.value.trim()

        if (name.isBlank() || email.isBlank()) return

        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val docRef = firestore.collection("team_staff").document()
                val member = AdminTeamMember(
                    id = docRef.id,
                    name = name,
                    email = email,
                    role = role,
                    genderCategory = gender,
                    supportTimings = timings,
                    status = "ACTIVE",
                    availability = "AVAILABLE"
                )
                docRef.set(member).await()

                logAdminAction("ADD_STAFF", "Added staff member: $name ($email) as $role", docRef.id)

                newMemberName.value = ""
                newMemberEmail.value = ""
                isLoading.value = false
                successMessage.value = "Staff member $name added successfully!"
                onSuccess()
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = "Failed to add staff: ${e.localizedMessage}"
            }
        }
    }

    fun toggleStaffStatus(member: AdminTeamMember) {
        val newStatus = if (member.status == "ACTIVE") "INACTIVE" else "ACTIVE"
        viewModelScope.launch {
            try {
                firestore.collection("team_staff").document(member.id)
                    .update("status", newStatus).await()
                logAdminAction("TOGGLE_STAFF_STATUS", "Set staff ${member.name} status to $newStatus", member.id)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update staff status: ${e.localizedMessage}"
            }
        }
    }

    fun deleteTeamMember(id: String, name: String) {
        viewModelScope.launch {
            try {
                firestore.collection("team_staff").document(id).delete().await()
                logAdminAction("REMOVE_STAFF", "Removed staff member: $name", id)
            } catch (e: Exception) {
                errorMessage.value = "Failed to remove staff: ${e.localizedMessage}"
            }
        }
    }

    // 3. User Governance
    fun fetchUsers() {
        firestore.collection("users")
            .limit(50)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(User::class.java)
                    userList.clear()
                    userList.addAll(list)
                }
            }
    }

    fun toggleUserSuspendStatus(user: User, reason: String) {
        val newStatus = if (user.accountStatus == "SUSPENDED") "ACTIVE" else "SUSPENDED"
        viewModelScope.launch {
            try {
                firestore.collection("users").document(user.uid)
                    .update(
                        mapOf(
                            "accountStatus" to newStatus,
                            "flagReason" to reason
                        )
                    ).await()
                logAdminAction("SUSPEND_USER", "Updated user ${user.email} status to $newStatus. Reason: $reason", user.uid)
                successMessage.value = "User status updated to $newStatus"
            } catch (e: Exception) {
                errorMessage.value = "Failed to update user status: ${e.localizedMessage}"
            }
        }
    }

    fun deleteUserAccount(uid: String, email: String) {
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid).delete().await()
                logAdminAction("DELETE_USER", "Permanently deleted user account: $email", uid)
                successMessage.value = "User account deleted successfully."
            } catch (e: Exception) {
                errorMessage.value = "Failed to delete user account: ${e.localizedMessage}"
            }
        }
    }

    // 4. Support Ticket Management
    private fun fetchSupportTickets() {
        firestore.collection("support_tickets")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(SupportTicket::class.java)
                    supportTickets.clear()
                    supportTickets.addAll(list)
                }
            }
    }

    fun reassignTicket(ticketId: String, staffName: String, staffEmail: String) {
        viewModelScope.launch {
            try {
                firestore.collection("support_tickets").document(ticketId)
                    .update(
                        mapOf(
                            "assignedStaff" to staffName,
                            "assignedStaffEmail" to staffEmail,
                            "status" to "IN_PROGRESS"
                        )
                    ).await()
                logAdminAction("REASSIGN_TICKET", "Reassigned ticket $ticketId to $staffName ($staffEmail)", ticketId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to reassign ticket: ${e.localizedMessage}"
            }
        }
    }

    fun updateTicketStatus(ticketId: String, status: String) {
        viewModelScope.launch {
            try {
                firestore.collection("support_tickets").document(ticketId)
                    .update("status", status).await()
                logAdminAction("UPDATE_TICKET_STATUS", "Updated ticket $ticketId status to $status", ticketId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update ticket status: ${e.localizedMessage}"
            }
        }
    }

    // 5. AI & App Config
    private fun fetchAppConfig() {
        firestore.collection("app_config").document("global_settings")
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
                    val config = doc.toObject(AppConfig::class.java)
                    if (config != null) {
                        appConfig = config
                    }
                }
            }
    }

    fun saveAppConfig(newConfig: AppConfig) {
        viewModelScope.launch {
            try {
                // Save version in ConfigHistory
                val docRef = firestore.collection("config_history").document()
                val historyItem = ConfigHistory(
                    id = docRef.id,
                    version = configHistoryList.size + 1,
                    previousConfigSummary = "AI: ${appConfig.aiEnabled}, Model: ${appConfig.selectedAiModel}, Maint: ${appConfig.maintenanceMode}",
                    newConfigSummary = "AI: ${newConfig.aiEnabled}, Model: ${newConfig.selectedAiModel}, Maint: ${newConfig.maintenanceMode}",
                    changedBy = currentAdminEmail,
                    timestamp = Timestamp.now()
                )
                docRef.set(historyItem).await()

                firestore.collection("app_config").document("global_settings").set(newConfig).await()
                appConfig = newConfig
                logAdminAction("UPDATE_APP_CONFIG", "Updated global app config", "global_settings")
                successMessage.value = "Global App Configuration updated successfully!"
            } catch (e: Exception) {
                errorMessage.value = "Failed to update configuration: ${e.localizedMessage}"
            }
        }
    }

    // 6. Configuration History & Rollback
    private fun fetchConfigHistory() {
        firestore.collection("config_history")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(ConfigHistory::class.java)
                    configHistoryList.clear()
                    configHistoryList.addAll(list)
                }
            }
    }

    // 7. Feedback Management
    private fun fetchFeedbackItems() {
        firestore.collection("feedback_items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(FeedbackItem::class.java)
                    feedbackList.clear()
                    feedbackList.addAll(list)
                }
            }
    }

    fun updateFeedbackStatus(feedbackId: String, status: String) {
        viewModelScope.launch {
            try {
                firestore.collection("feedback_items").document(feedbackId)
                    .update("status", status).await()
                logAdminAction("UPDATE_FEEDBACK_STATUS", "Updated feedback $feedbackId status to $status", feedbackId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update feedback: ${e.localizedMessage}"
            }
        }
    }

    // 8. Security Center
    private fun fetchSecurityAlerts() {
        firestore.collection("security_alerts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(SecurityAlert::class.java)
                    securityAlertsList.clear()
                    securityAlertsList.addAll(list)
                }
            }
    }

    // 9. Diagnostics & Testing Sandbox
    fun testGroqConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = groqService.getChatResponse("Ping test connection.")
                if (res.isNotBlank()) {
                    onResult(true, "Groq AI Live Connection Active! Response: \"${res.take(40)}...\"")
                } else {
                    onResult(false, "Groq AI returned an empty test response.")
                }
            } catch (e: Exception) {
                onResult(false, "Groq AI Connection Error: ${e.localizedMessage}")
            }
        }
    }

    // 10. Emergency Operations
    fun toggleEmergencyMaintenanceMode() {
        val updatedConfig = appConfig.copy(maintenanceMode = !appConfig.maintenanceMode)
        saveAppConfig(updatedConfig)
        logAdminAction("EMERGENCY_TOGGLE_MAINTENANCE", "Toggled Maintenance Mode to ${updatedConfig.maintenanceMode}", "global_settings")
    }

    // 11. Notifications & Announcements
    private fun fetchAnnouncements() {
        firestore.collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(Announcement::class.java)
                    announcements.clear()
                    announcements.addAll(list)
                }
            }
    }

    fun sendAnnouncement(onSuccess: () -> Unit) {
        val title = announcementTitle.value.trim()
        val body = announcementBody.value.trim()
        val target = announcementTarget.value

        if (title.isBlank() || body.isBlank()) return

        viewModelScope.launch {
            try {
                val docRef = firestore.collection("announcements").document()
                val announcement = Announcement(
                    id = docRef.id,
                    title = title,
                    body = body,
                    targetGroup = target,
                    authorEmail = currentAdminEmail
                )
                docRef.set(announcement).await()
                logAdminAction("BROADCAST_ANNOUNCEMENT", "Sent announcement '$title' to $target", docRef.id)

                announcementTitle.value = ""
                announcementBody.value = ""
                successMessage.value = "Broadcast announcement sent!"
                onSuccess()
            } catch (e: Exception) {
                errorMessage.value = "Failed to send announcement: ${e.localizedMessage}"
            }
        }
    }

    // 12. Safety Center & Abuse Reports
    private fun fetchAbuseReports() {
        firestore.collection("abuse_reports")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(AbuseReport::class.java)
                    abuseReports.clear()
                    abuseReports.addAll(list)
                }
            }
    }

    fun updateReportStatus(reportId: String, status: String) {
        viewModelScope.launch {
            try {
                firestore.collection("abuse_reports").document(reportId)
                    .update("status", status).await()
                logAdminAction("UPDATE_ABUSE_REPORT", "Updated report $reportId status to $status", reportId)
            } catch (e: Exception) {
                errorMessage.value = "Failed to update report: ${e.localizedMessage}"
            }
        }
    }

    // Audit Logs
    private fun fetchAuditLogs() {
        firestore.collection("admin_audit_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.toObjects(AdminAuditLog::class.java)
                    auditLogs.clear()
                    auditLogs.addAll(list)
                }
            }
    }

    private fun logAdminAction(action: String, details: String, recordId: String) {
        viewModelScope.launch {
            try {
                val docRef = firestore.collection("admin_audit_logs").document()
                val log = AdminAuditLog(
                    id = docRef.id,
                    actorEmail = currentAdminEmail,
                    action = action,
                    details = details,
                    affectedRecordId = recordId,
                    timestamp = Timestamp.now()
                )
                docRef.set(log)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
