package harshzynx.saathi.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Public
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
import harshzynx.saathi.data.model.CommunityPost
import harshzynx.saathi.ui.theme.GradientEnd
import harshzynx.saathi.ui.theme.GradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onBackClick: () -> Unit,
    onNavigateToSOS: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val posts by viewModel.posts
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Share Anonymously 🌍") },
            text = {
                OutlinedTextField(
                    value = viewModel.newPostContent.value,
                    onValueChange = { viewModel.newPostContent.value = it },
                    placeholder = { Text("Write something supportive...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addPost(
                        onSuccess = {
                            showAddDialog = false
                            Toast.makeText(context, "Shared with community!", Toast.LENGTH_SHORT).show()
                        },
                        onUnsafe = {
                            showAddDialog = false
                            Toast.makeText(context, "Saathi cares. Please use the SOS helpline.", Toast.LENGTH_LONG).show()
                            onNavigateToSOS()
                        }
                    )
                }) {
                    Text("Post Anonymously")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Safe Community 🌍") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Post")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(listOf(GradientStart, GradientEnd)))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "You are not alone. Read reflections from other anonymous 'Saathis'. All posts are AI-moderated for safety.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(posts) { post ->
                    CommunityPostItem(post)
                }
            }
        }
    }
}

@Composable
fun CommunityPostItem(post: CommunityPost) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Anonymous Saathi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.content, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Supportive community", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
