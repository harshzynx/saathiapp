package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ActivityItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val routeKey: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    onBackClick: () -> Unit,
    onNavigateToSounds: () -> Unit
) {
    var activeActivity by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val items = listOf(
        ActivityItem("Breathing", "Calm your mind", Icons.Default.Air, Color(0xFFE0F2F1), "breathing"),
        ActivityItem("Meditation", "Find your peace", Icons.Default.SelfImprovement, Color(0xFFF3E5F5), "meditation"),
        ActivityItem("Sleep Sounds", "Relax & sleep", Icons.Default.Nightlight, Color(0xFFE3F2FD), "sounds"),
        ActivityItem("Gratitude", "Be positive", Icons.Default.Favorite, Color(0xFFFCE4EC), "gratitude"),
        ActivityItem("9 Mind Games", "Level 1 to 20 Games", Icons.Default.Extension, Color(0xFFFFF9C4), "games")
    )

    when (activeActivity) {
        "breathing" -> {
            BreathingDetailView(onBack = { activeActivity = null })
            return
        }
        "games" -> {
            MindGamesDetailView(onBack = { activeActivity = null })
            return
        }
        "meditation" -> {
            MeditationDetailView(onBack = { activeActivity = null })
            return
        }
        "gratitude" -> {
            GratitudeDetailView(onBack = { activeActivity = null })
            return
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activities 🌿") },
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
                .padding(20.dp)
        ) {
            Text("Activities", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("Better mind. Better you.", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { act ->
                    ActivityCard(act) {
                        when (act.routeKey) {
                            "sounds" -> onNavigateToSounds()
                            "breathing" -> activeActivity = "breathing"
                            "games" -> activeActivity = "games"
                            "meditation" -> activeActivity = "meditation"
                            "gratitude" -> activeActivity = "gratitude"
                            else -> Toast.makeText(context, "Opening ${act.title}...", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Small steps everyday", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                        Text("Make a big difference 💙", fontSize = 12.sp, color = Color.Gray)
                    }
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun ActivityCard(item: ActivityItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = item.color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(item.icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
            Text(item.subtitle, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

// -------------------------------------------------------------
// 9 LEVEL-BASED MIND GAMES HUB
// -------------------------------------------------------------
data class GameMenuItem(
    val id: Int,
    val title: String,
    val desc: String,
    val iconEmoji: String,
    val bgGradient: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindGamesDetailView(onBack: () -> Unit) {
    var selectedGameId by remember { mutableIntStateOf(0) }

    val gameList = listOf(
        GameMenuItem(1, "Memory Match", "Level 1 to 20 — Emoji Pair Match", "🧩", listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))),
        GameMenuItem(2, "Classic Snake Game", "Level 1 to 20 — D-Pad Controls & Apples", "🐍", listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))),
        GameMenuItem(3, "Bingo Mind Grid", "Level 1 to 20 — Draw Numbers & Mark Grid", "🔢", listOf(Color(0xFFE1F5FE), Color(0xFF81D4FA))),
        GameMenuItem(4, "Sliding Tile Puzzle", "Level 1 to 20 — Interactive 1 to 8 Sliding Tile", "🧩", listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80))),
        GameMenuItem(5, "Positive Word Unscramble", "Level 1 to 20 — Unscramble Mindful Words", "🔤", listOf(Color(0xFFF3E5F5), Color(0xFFCE93D8))),
        GameMenuItem(6, "Bubble Pop Reliever", "Level 1 to 20 — Pop Target Stress Bubbles", "🫧", listOf(Color(0xFFE0F7FA), Color(0xFF80DEEA))),
        GameMenuItem(7, "Lotus Focus Tap", "Level 1 to 20 — Catch Glowing Lotus", "🌸", listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))),
        GameMenuItem(8, "Thought Dissolver", "Level 1 to 20 — Burst Overthinking Loops", "☁️", listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))),
        GameMenuItem(9, "Zen Stone Balancer", "Stack & Balance Zen Stones", "🪨", listOf(Color(0xFFFFF8E1), Color(0xFFFFE082)))
    )

    if (selectedGameId > 0) {
        when (selectedGameId) {
            1 -> Game1MemoryMatchLevels(onBack = { selectedGameId = 0 })
            2 -> SnakeGameLevels(onBack = { selectedGameId = 0 })
            3 -> BingoGameLevels(onBack = { selectedGameId = 0 })
            4 -> SlidingTilePuzzleLevels(onBack = { selectedGameId = 0 })
            5 -> WordUnscrambleLevels(onBack = { selectedGameId = 0 })
            6 -> Game2BubblePopLevels(onBack = { selectedGameId = 0 })
            7 -> Game4LotusFocusLevels(onBack = { selectedGameId = 0 })
            8 -> Game3ThoughtDissolverLevels(onBack = { selectedGameId = 0 })
            9 -> Game5ZenBalancer(onBack = { selectedGameId = 0 })
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("9 Mind Games — Interactive 🧠") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFFFF9C4), GradientEnd)))
                .padding(16.dp)
        ) {
            Text("Select a Mind Game", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            Text("Interactive games with Level 1 to 20 progression ✨", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(gameList) { game ->
                    Card(
                        onClick = { selectedGameId = game.id },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = game.bgGradient[0],
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(game.iconEmoji, fontSize = 26.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(game.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                                Text(game.desc, fontSize = 12.sp, color = Color.Gray)
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. MEMORY MATCH (LEVEL 1 TO 20)
// -------------------------------------------------------------
data class MemoryCard(val id: Int, val emoji: String, var isFlipped: Boolean = false, var isMatched: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game1MemoryMatchLevels(onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var currentLevel by remember { mutableIntStateOf(1) }

    val levelEmojis = remember(currentLevel) {
        when {
            currentLevel <= 2 -> listOf("🌸", "🌸", "🌿", "🌿")
            currentLevel <= 5 -> listOf("🌸", "🌸", "🌿", "🌿", "☀️", "☀️")
            currentLevel <= 10 -> listOf("🌸", "🌸", "🌿", "🌿", "☀️", "☀️", "🌊", "🌊")
            currentLevel <= 15 -> listOf("🌸", "🌸", "🌿", "🌿", "☀️", "☀️", "🌊", "🌊", "🪷", "🪷", "🎨", "🎨")
            else -> listOf("🌸", "🌸", "🌿", "🌿", "☀️", "☀️", "🌊", "🌊", "🪷", "🪷", "🎨", "🎨", "⭐", "⭐", "🍀", "🍀")
        }
    }

    var cards by remember(currentLevel) {
        mutableStateOf(levelEmojis.shuffled().mapIndexed { i, e -> MemoryCard(i, e) })
    }
    var sel1 by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableIntStateOf(0) }
    var isProcessing by remember { mutableStateOf(false) }

    val isLevelComplete = cards.all { it.isMatched }

    fun onCardClick(index: Int) {
        if (isProcessing || cards[index].isFlipped || cards[index].isMatched) return
        val list = cards.toMutableList()
        list[index] = list[index].copy(isFlipped = true)
        cards = list

        if (sel1 == null) {
            sel1 = index
        } else {
            val idx1 = sel1!!
            moves++
            isProcessing = true
            coroutineScope.launch {
                delay(600)
                val updated = cards.toMutableList()
                if (updated[idx1].emoji == updated[index].emoji) {
                    updated[idx1] = updated[idx1].copy(isMatched = true)
                    updated[index] = updated[index].copy(isMatched = true)
                } else {
                    updated[idx1] = updated[idx1].copy(isFlipped = false)
                    updated[index] = updated[index].copy(isFlipped = false)
                }
                cards = updated
                sel1 = null
                isProcessing = false
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Memory Match — Level $currentLevel / 20 🧩") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                Text(
                    text = "Level $currentLevel  •  Moves: $moves  •  Pairs: ${cards.count { it.isMatched } / 2} / ${levelEmojis.size / 2}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (isLevelComplete) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 Level $currentLevel Cleared! ✨", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (currentLevel < 20) "Great job! Ready for Next Level?" else "🏆 Master Level 20 Cleared!",
                            fontSize = 14.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (currentLevel < 20) {
                                    currentLevel++
                                    moves = 0
                                } else {
                                    currentLevel = 1
                                    moves = 0
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (currentLevel < 20) "Next Level ${currentLevel + 1} ➔" else "Restart From Level 1 🔄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(4), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(cards.size) { i ->
                        Card(modifier = Modifier.height(80.dp).clickable { onCardClick(i) }, shape = RoundedCornerShape(16.dp)) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(if (cards[i].isFlipped || cards[i].isMatched) cards[i].emoji else "🌱", fontSize = 26.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CLASSIC SNAKE GAME (LEVELS 1 TO 20)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnakeGameLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    var isRunning by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    val gridSize = 8
    var snake by remember(level) { mutableStateOf(listOf(Pair(3, 3), Pair(3, 2))) }
    var direction by remember(level) { mutableStateOf(Pair(0, 1)) }
    var food by remember(level) { mutableStateOf(Pair(5, 5)) }
    var applesEaten by remember(level) { mutableIntStateOf(0) }

    val targetApples = 2 + level

    val isLevelComplete = applesEaten >= targetApples

    LaunchedEffect(isRunning, direction, level) {
        while (isRunning && !isLevelComplete && !isGameOver) {
            delay((280 - (level * 8)).coerceAtLeast(100).toLong())

            val head = snake.first()
            val newHead = Pair(
                (head.first + direction.first + gridSize) % gridSize,
                (head.second + direction.second + gridSize) % gridSize
            )

            if (snake.contains(newHead)) {
                isGameOver = true
                isRunning = false
            } else {
                val newSnake = mutableListOf(newHead)
                if (newHead == food) {
                    applesEaten++
                    newSnake.addAll(snake)
                    var nextFood = Pair((0 until gridSize).random(), (0 until gridSize).random())
                    while (newSnake.contains(nextFood)) {
                        nextFood = Pair((0 until gridSize).random(), (0 until gridSize).random())
                    }
                    food = nextFood
                } else {
                    newSnake.addAll(snake.dropLast(1))
                }
                snake = newSnake
            }
        }
    }

    fun restartLevel() {
        snake = listOf(Pair(3, 3), Pair(3, 2))
        direction = Pair(0, 1)
        food = Pair(5, 5)
        applesEaten = 0
        isGameOver = false
        isRunning = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Snake Game — Level $level / 20 🐍") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                Text(
                    text = "Level $level Goal: $applesEaten / $targetApples Apples 🍎",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLevelComplete) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 Level $level Completed! 🐍", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (level < 20) {
                                    level++
                                    restartLevel()
                                } else {
                                    level = 1
                                    restartLevel()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart From Level 1 🔄")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .size(250.dp)
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(20.dp))
                        .padding(8.dp)
                ) {
                    for (r in 0 until gridSize) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (c in 0 until gridSize) {
                                val pos = Pair(r, c)
                                val isHead = snake.firstOrNull() == pos
                                val isBody = snake.contains(pos)
                                val isFood = food == pos

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(1.dp)
                                        .background(
                                            when {
                                                isHead -> Color(0xFF2E7D32)
                                                isBody -> Color(0xFF4CAF50)
                                                isFood -> Color(0xFFE53935)
                                                else -> Color(0xFFC8E6C9)
                                            },
                                            RoundedCornerShape(4.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isFood) Text("🍎", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isRunning && !isGameOver) {
                    Button(onClick = { isRunning = true }, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(16.dp)) {
                        Text("Start Level $level ▶️", fontWeight = FontWeight.Bold)
                    }
                } else if (isGameOver) {
                    Text("Oops! Try again!", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(onClick = { restartLevel() }) {
                        Text("Retry Level $level 🔄")
                    }
                }

                // D-Pad Controls
                Spacer(modifier = Modifier.height(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { if (direction != Pair(1, 0)) direction = Pair(-1, 0) }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", modifier = Modifier.size(36.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        IconButton(onClick = { if (direction != Pair(0, 1)) direction = Pair(0, -1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left", modifier = Modifier.size(36.dp))
                        }
                        IconButton(onClick = { if (direction != Pair(0, -1)) direction = Pair(0, 1) }) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Right", modifier = Modifier.size(36.dp))
                        }
                    }
                    IconButton(onClick = { if (direction != Pair(-1, 0)) direction = Pair(1, 0) }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", modifier = Modifier.size(36.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. FULLY INTERACTIVE BINGO MIND GRID (LEVEL 1 TO 20)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BingoGameLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    var gridNumbers by remember(level) { mutableStateOf((1..25).shuffled()) }
    var markedIndices by remember(level) { mutableStateOf(setOf<Int>()) }
    var currentCalledNum by remember(level) { mutableStateOf<Int?>(null) }
    var drawnHistory by remember(level) { mutableStateOf(listOf<Int>()) }

    // Target marks to win Level (3 to 5 numbers)
    val targetMarks = (3 + (level % 3)).coerceAtMost(5)
    val isBingoWin = markedIndices.size >= targetMarks

    fun drawNextNumber() {
        val remaining = gridNumbers.filter { it !in drawnHistory }
        if (remaining.isNotEmpty()) {
            val num = remaining.random()
            currentCalledNum = num
            drawnHistory = drawnHistory + num
            // Auto mark matching number on grid if found!
            val foundIdx = gridNumbers.indexOf(num)
            if (foundIdx != -1) {
                markedIndices = markedIndices + foundIdx
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bingo Grid — Level $level / 20 🔢") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                Text(
                    text = "Level $level • Marked: ${markedIndices.size} / $targetMarks • Tap Draw or Tap any card tile!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isBingoWin) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 BINGO! Level $level Cleared! ✨", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Great job! Your focus & mood is refreshed.", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                if (level < 20) level++ else level = 1
                                markedIndices = setOf()
                                drawnHistory = listOf()
                                currentCalledNum = null
                                gridNumbers = (1..25).shuffled()
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart From Level 1 🔄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { drawNextNumber() },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Draw Number 🎲", fontWeight = FontWeight.Bold)
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (currentCalledNum != null) "Drawn: #$currentCalledNum 🔴" else "Tap Draw 🎲",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5x5 Grid Card - Tap ANY tile to mark/unmark directly!
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(25) { idx ->
                        val num = gridNumbers[idx]
                        val isMarked = markedIndices.contains(idx)
                        val isCurrentDrawn = num == currentCalledNum

                        Card(
                            onClick = {
                                // Tap ANY tile on the card to toggle mark!
                                markedIndices = if (isMarked) markedIndices - idx else markedIndices + idx
                            },
                            modifier = Modifier.height(58.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isMarked -> MaterialTheme.colorScheme.primary
                                    isCurrentDrawn -> MaterialTheme.colorScheme.secondary
                                    else -> Color.White
                                }
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isMarked) "✓ $num" else num.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isMarked || isCurrentDrawn) Color.White else Color(0xFF1E293B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. FULLY INTERACTIVE SLIDING TILE PUZZLE (LEVEL 1 TO 20)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlidingTilePuzzleLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    var movesCount by remember(level) { mutableIntStateOf(0) }
    var tiles by remember(level) { mutableStateOf(generateSolvableTiles(level)) }

    val winOrder = listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    val isSolved = tiles == winOrder

    fun slideTile(idx: Int) {
        val emptyIdx = tiles.indexOf(0)
        val rowIdx = idx / 3
        val colIdx = idx % 3
        val emptyRow = emptyIdx / 3
        val emptyCol = emptyIdx % 3

        // Adjacent check (up, down, left, right)
        val isAdjacent = (rowIdx == emptyRow && Math.abs(colIdx - emptyCol) == 1) ||
                (colIdx == emptyCol && Math.abs(rowIdx - emptyRow) == 1)

        if (isAdjacent) {
            val newList = tiles.toMutableList()
            newList[emptyIdx] = tiles[idx]
            newList[idx] = 0
            tiles = newList
            movesCount++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tile Puzzle — Level $level / 20 🧩") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 2.dp) {
                Text(
                    text = "Level $level • Moves: $movesCount • Tap tiles next to empty slot to slide! 🧩",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isSolved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 Puzzle Solved in $movesCount moves! ✨", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Level $level Cleared! Sharp mind!", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                if (level < 20) level++ else level = 1
                                movesCount = 0
                                tiles = generateSolvableTiles(level)
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart From Level 1 🔄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.size(270.dp)
                ) {
                    items(9) { idx ->
                        val num = tiles[idx]
                        if (num != 0) {
                            Card(
                                onClick = { slideTile(idx) },
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(num.toString(), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        movesCount = 0
                        tiles = generateSolvableTiles(level)
                    },
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Shuffle Puzzle 🔀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Generate guaranteed 100% solvable tile board with valid slides from win state
fun generateSolvableTiles(level: Int): List<Int> {
    val current = mutableListOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    val shuffleSteps = (4 + level * 2).coerceAtMost(30)
    var lastIdx = -1

    for (step in 0 until shuffleSteps) {
        val emptyIdx = current.indexOf(0)
        val row = emptyIdx / 3
        val col = emptyIdx % 3

        val validNeighbors = mutableListOf<Int>()
        if (row > 0) validNeighbors.add(emptyIdx - 3)
        if (row < 2) validNeighbors.add(emptyIdx + 3)
        if (col > 0) validNeighbors.add(emptyIdx - 1)
        if (col < 2) validNeighbors.add(emptyIdx + 1)

        validNeighbors.remove(lastIdx) // avoid immediate undo
        val targetIdx = if (validNeighbors.isNotEmpty()) validNeighbors.random() else (emptyIdx - 1).coerceAtLeast(0)

        current[emptyIdx] = current[targetIdx]
        current[targetIdx] = 0
        lastIdx = emptyIdx
    }
    return current
}

// -------------------------------------------------------------
// 5. WORD UNSCRAMBLE (LEVELS 1 TO 20)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordUnscrambleLevels(onBack: () -> Unit) {
    val levelWords = remember {
        listOf(
            "CALM" to "LACM",
            "PEACE" to "EAPCE",
            "SAATHI" to "HSAATI",
            "RELAX" to "XALER",
            "HAPPY" to "PHAPY",
            "STRENGTH" to "TSRNETGH",
            "FREEDOM" to "REDFOEM",
            "COURAGE" to "ROUCAGE",
            "SERENITY" to "ESERINYT",
            "CONFIDENCE" to "CNODFECIEN",
            "JOYFUL" to "YOFJLU",
            "HARMONY" to "MAHRONY",
            "SMILE" to "LISEM",
            "WISDOM" to "IWDSOM",
            "ENERGY" to "REGNYE",
            "FOCUS" to "SUFCO",
            "BLESSING" to "SELBSING",
            "KINDNESS" to "DNIKNESS",
            "BALANCE" to "ABALCNE",
            "SUCCESS" to "CSUCSSE"
        )
    }
    var level by remember { mutableIntStateOf(1) }
    var input by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val current = levelWords[(level - 1) % levelWords.size]

    Scaffold(topBar = { TopAppBar(title = { Text("Word Puzzle — Level $level / 20 🔤") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                Text("Level $level / 20 Positive Word 🔤", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("Unscramble: ${current.second}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Type word...") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (input.trim().equals(current.first, ignoreCase = true)) {
                    message = "Correct! 🎉 Level $level Cleared!"
                    input = ""
                    if (level < 20) level++ else level = 1
                } else {
                    message = "Try again! Hint: Starts with ${current.first.take(1)}"
                }
            }) { Text("Check Word & Next Level ➔") }
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
        }
    }
}

// -------------------------------------------------------------
// BUBBLE POP
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game2BubblePopLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    val targetBubbles = level * 6
    var poppedCount by remember { mutableIntStateOf(0) }

    val colors = listOf(Color(0xFF81D4FA), Color(0xFFCE93D8), Color(0xFFA5D6A7), Color(0xFFFFCC80))

    val isLevelComplete = poppedCount >= targetBubbles

    Scaffold(topBar = { TopAppBar(title = { Text("Bubble Pop — Level $level / 20 🫧") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                Text("Goal: $poppedCount / $targetBubbles Bubbles 🫧", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (isLevelComplete) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 Level $level Completed! ✨", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            if (level < 20) {
                                level++
                                poppedCount = 0
                            } else {
                                level = 1
                                poppedCount = 0
                            }
                        }) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart 🔄")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(3), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(9) { i ->
                        Surface(
                            shape = CircleShape,
                            color = colors[i % colors.size],
                            modifier = Modifier.size(84.dp).clickable { poppedCount++ },
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🫧", fontSize = 32.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// THOUGHT DISSOLVER
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game3ThoughtDissolverLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }

    val levelThoughtList = remember(level) {
        when (level % 5) {
            1 -> mutableStateListOf("Exam Panic ⚡", "Self Doubt 💭")
            2 -> mutableStateListOf("Overthinking Loop 🌀", "Fear of Failure ⏳", "Tired Mind 🌧️")
            3 -> mutableStateListOf("Comparison with Others 📈", "Social Anxiety 👥", "Regret 💭", "Lack of Sleep 🌙")
            4 -> mutableStateListOf("Future Stress 🔮", "Academic Pressure 📚", "Low Energy ⚡", "Perfectionism 🎯", "Negative Thoughts 💭")
            else -> mutableStateListOf("All Stress 🌩️", "Self Uncertainty 🌧️", "Overwhelm 🌀", "Mind Clutter 💭", "Restlessness ⚡", "Burnout ☕")
        }
    }

    val isLevelComplete = levelThoughtList.isEmpty()

    Scaffold(topBar = { TopAppBar(title = { Text("Thought Dissolver — Level $level / 20 ☁️") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tap negative thoughts to burst them", fontSize = 15.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLevelComplete) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✨ Level $level Mind Uncluttered!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            if (level < 20) level++ else level = 1
                        }) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart From Level 1 🔄")
                        }
                    }
                }
            } else {
                levelThoughtList.forEach { thought ->
                    Card(
                        onClick = { levelThoughtList.remove(thought) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                    ) {
                        Text(thought, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LOTUS FOCUS
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game4LotusFocusLevels(onBack: () -> Unit) {
    var level by remember { mutableIntStateOf(1) }
    val targetTaps = level * 4
    var currentTaps by remember { mutableIntStateOf(0) }

    val isLevelComplete = currentTaps >= targetTaps

    Scaffold(topBar = { TopAppBar(title = { Text("Lotus Focus — Level $level / 20 🌸") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 1.dp) {
                Text("Target: $currentTaps / $targetTaps Focus 🪷", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(30.dp))

            if (isLevelComplete) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉 Focus Level $level Achieved! ✨", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            if (level < 20) {
                                level++
                                currentTaps = 0
                            } else {
                                level = 1
                                currentTaps = 0
                            }
                        }) {
                            Text(if (level < 20) "Next Level ${level + 1} ➔" else "Restart 🔄")
                        }
                    }
                }
            } else {
                Surface(shape = CircleShape, color = Color(0xFFFCE4EC), modifier = Modifier.size(160.dp).clickable { currentTaps++ }) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🪷", fontSize = 72.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("Tap lotus smoothly to build focus", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// ZEN BALANCER
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game5ZenBalancer(onBack: () -> Unit) {
    var balanceSlider by remember { mutableFloatStateOf(0.5f) }

    Scaffold(topBar = { TopAppBar(title = { Text("Zen Stone Balancer 🪨") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } }) }) { pad ->
        Column(modifier = Modifier.fillMaxSize().padding(pad).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(if (balanceSlider in 0.45f..0.55f) "✨ Balanced! Perfect Peace" else "Balance the stones", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(30.dp))
            Text("🪨\n🪨\n🪨", fontSize = 48.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(30.dp))
            Slider(value = balanceSlider, onValueChange = { balanceSlider = it }, valueRange = 0f..1f, modifier = Modifier.fillMaxWidth())
        }
    }
}

// -------------------------------------------------------------
// BREATHING
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingDetailView(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Breathing Exercise 🫁") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("4-4-4 Technique", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Follow the expanding and shrinking circle.", color = Color.Gray, fontSize = 14.sp)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            BreathingCircle()
            
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Finish Session")
            }
        }
    }
}

@Composable
fun BreathingCircle() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(180.dp)
            .scale(scale)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (scale > 1.3f) "Inhale" else "Exhale",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// -------------------------------------------------------------
// MEDITATION
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationDetailView(onBack: () -> Unit) {
    var isRunning by remember { mutableStateOf(false) }
    var secondsLeft by remember { mutableIntStateOf(120) }

    LaunchedEffect(isRunning) {
        while (isRunning && secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
        if (secondsLeft == 0) isRunning = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mindful Meditation 🧘") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFF3E5F5), GradientEnd)))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("2-Minute Mindfulness", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Close your eyes, relax your shoulders, and focus on your breath.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(180.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val minutes = secondsLeft / 60
                    val secs = secondsLeft % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, secs),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { isRunning = !isRunning },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (isRunning) "Pause Session ⏸️" else if (secondsLeft == 0) "Completed ✨" else "Start Meditation ▶️", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// -------------------------------------------------------------
// GRATITUDE
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeDetailView(onBack: () -> Unit) {
    var gratitudeText by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Gratitude 💛") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .background(Brush.verticalGradient(listOf(GradientStart, Color(0xFFFCE4EC), GradientEnd)))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("3 Good Things Today", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("What made you smile or feel thankful today?", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            if (isSaved) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💖 Saved to your heart!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Focusing on gratitude brings peace to your mind.", fontSize = 13.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) {
                            Text("Done")
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = gratitudeText,
                    onValueChange = { gratitudeText = it },
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    placeholder = { Text("1. A hot cup of tea...\n2. A nice conversation...\n3. Fresh morning air...") },
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { if (gratitudeText.isNotBlank()) isSaved = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = gratitudeText.isNotBlank()
                ) {
                    Text("Save Gratitude ✨", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
