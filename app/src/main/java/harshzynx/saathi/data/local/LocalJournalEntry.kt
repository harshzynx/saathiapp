package harshzynx.saathi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class LocalJournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val aiReflection: String?,
    val timestamp: Long = System.currentTimeMillis()
)
