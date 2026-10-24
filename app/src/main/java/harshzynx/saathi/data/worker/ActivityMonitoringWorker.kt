package harshzynx.saathi.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import harshzynx.saathi.data.repository.AuthRepository
import harshzynx.saathi.data.repository.WellbeingRepository
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

@HiltWorker
class ActivityMonitoringWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val authRepository: AuthRepository,
    private val wellbeingRepository: WellbeingRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val user = authRepository.getUserProfile() ?: return Result.success()
        
        val lastActive = user.lastActiveTimestamp ?: return Result.success()
        val currentTime = Timestamp.now()
        
        val diffInMs = currentTime.seconds * 1000 - lastActive.seconds * 1000
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMs)
        
        if (diffInDays >= 4 && user.isGuardianLinked && user.trustedPersonEmail != null) {
            // Trigger logic to notify guardian about activity drop
            // In a real app, this might be a server-side function. 
            // Here we just mark it or simulate.
        }
        
        return Result.success()
    }
}
