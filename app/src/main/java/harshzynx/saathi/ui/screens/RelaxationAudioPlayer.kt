package harshzynx.saathi.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
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
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelaxationAudioPlayer(onBackClick: () -> Unit) {
    var currentTrack by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    
    // Ambient Mixer Sliders
    var rainVol by remember { mutableFloatStateOf(0.7f) }
    var forestVol by remember { mutableFloatStateOf(0.4f) }
    var oceanVol by remember { mutableFloatStateOf(0.5f) }
    var pianoVol by remember { mutableFloatStateOf(0.6f) }

    val context = LocalContext.current
    
    val mediaPlayer = remember { 
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build(),
            )
            setOnErrorListener { _, _, _ ->
                Toast.makeText(context, "Could not play audio. Check internet connection.", Toast.LENGTH_SHORT).show()
                isPlaying = false
                true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    fun playTrack(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                it.start()
                isPlaying = true
            }
        } catch (e: Exception) {
            isPlaying = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ambient Sound Mixer 🎵") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFE3F2FD), GradientEnd)))
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = currentTrack ?: "Select a Preset Sound",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Mix rain, forest, ocean & piano for focus or sleep ✨",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            SoundButton("Rain on Window 🌧️", currentTrack) {
                currentTrack = "Rain on Window"
                playTrack("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
            }
            SoundButton("Summer Forest 🌲", currentTrack) {
                currentTrack = "Summer Forest"
                playTrack("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3")
            }
            SoundButton("Deep Ocean Waves 🌊", currentTrack) {
                currentTrack = "Deep Ocean Waves"
                playTrack("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Ambient Mixer Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Custom Sound Mixer 🎛️", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Rain Level: ${(rainVol * 100).toInt()}% 🌧️", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Slider(value = rainVol, onValueChange = { rainVol = it }, valueRange = 0f..1f)

                    Text("Forest Level: ${(forestVol * 100).toInt()}% 🌲", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Slider(value = forestVol, onValueChange = { forestVol = it }, valueRange = 0f..1f)

                    Text("Ocean Level: ${(oceanVol * 100).toInt()}% 🌊", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Slider(value = oceanVol, onValueChange = { oceanVol = it }, valueRange = 0f..1f)

                    Text("Soft Piano: ${(pianoVol * 100).toInt()}% 🎹", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Slider(value = pianoVol, onValueChange = { pianoVol = it }, valueRange = 0f..1f)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (currentTrack != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { 
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                        } else {
                            mediaPlayer.start()
                        }
                        isPlaying = mediaPlayer.isPlaying
                    }, modifier = Modifier.size(64.dp)) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SoundButton(title: String, selectedTrack: String?, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (title == selectedTrack) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (title == selectedTrack) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(title, fontWeight = FontWeight.Bold)
    }
}
