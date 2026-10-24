package harshzynx.saathi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: LocalJournalEntry): Long

    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<LocalJournalEntry>>

    @Query("UPDATE journal_entries SET aiReflection = :reflection WHERE id = :id")
    suspend fun updateReflection(id: Long, reflection: String)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun deleteAll()
}
