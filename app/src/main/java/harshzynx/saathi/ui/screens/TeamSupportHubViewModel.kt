package harshzynx.saathi.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import harshzynx.saathi.data.model.AdminTeamMember
import harshzynx.saathi.data.model.SupportRoom
import harshzynx.saathi.data.model.TeamMember
import javax.inject.Inject

@HiltViewModel
class TeamSupportHubViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _teamMembers = mutableStateListOf<TeamMember>()
    val teamMembers: List<TeamMember> = _teamMembers

    private val _supportRooms = mutableStateListOf<SupportRoom>()
    val supportRooms: List<SupportRoom> = _supportRooms

    init {
        fetchRealTeamMembers()
        fetchRealSupportRooms()
    }

    private fun fetchRealTeamMembers() {
        firestore.collection("team_staff")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val staffList = snapshot.toObjects(AdminTeamMember::class.java)
                    val mapped = staffList.map { staff ->
                        TeamMember(
                            id = staff.id,
                            name = staff.name,
                            gender = if (staff.role.contains("Female", true)) "Female" else "Male",
                            role = staff.role,
                            language = "Hindi / English",
                            isAvailable = true,
                            bio = "Authorized Saathi Support Staff - Ready to listen and help.",
                            tags = listOf(staff.role, "Support"),
                            avatarUrl = ""
                        )
                    }
                    _teamMembers.clear()
                    _teamMembers.addAll(mapped)
                }
            }
    }

    private fun fetchRealSupportRooms() {
        firestore.collection("support_rooms")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val rooms = snapshot.toObjects(SupportRoom::class.java)
                    _supportRooms.clear()
                    _supportRooms.addAll(rooms)
                } else {
                    // Seed initial support rooms into Firestore if empty
                    val defaultRooms = listOf(
                        SupportRoom("room_1", "College & Exam Stress", 18, "Academic"),
                        SupportRoom("room_2", "Overthinking & Peace", 12, "Mental Peace"),
                        SupportRoom("room_3", "Friendship & Social", 9, "Social"),
                        SupportRoom("room_4", "Just Listen & Vent Room", 7, "Vent")
                    )
                    defaultRooms.forEach { room ->
                        firestore.collection("support_rooms").document(room.id).set(room)
                    }
                }
            }
    }
}
