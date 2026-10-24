package harshzynx.saathi.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import harshzynx.saathi.data.local.JournalDao
import harshzynx.saathi.data.local.SaathiDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SaathiDatabase {
        return Room.databaseBuilder(
            context,
            SaathiDatabase::class.java,
            "saathi_db"
        ).build()
    }

    @Provides
    fun provideJournalDao(db: SaathiDatabase): JournalDao = db.journalDao()
}
