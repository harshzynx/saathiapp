package harshzynx.saathi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalJournalEntry::class], version = 1, exportSchema = false)
abstract class SaathiDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao
}
