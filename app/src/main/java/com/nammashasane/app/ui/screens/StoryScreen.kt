package com.nammashasane.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nammashasane.app.navigation.Screen
import com.nammashasane.app.viewmodel.InscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    inscriptionId: Int?,
    navController: NavController,
    viewModel: InscriptionViewModel
) {
    val inscription by viewModel.selectedInscription.collectAsState()
    val aiStory by viewModel.aiStory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(inscription?.title ?: "Inscription Story") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B6914), // ParchmentDark
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F0E8)) // StoneBeige
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFD4A843), Color(0xFFF5E6C8), Color(0xFFF5F0E8))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📜", fontSize = 64.sp)
                    Text(
                        text = inscription?.dynasty ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF8B6914)
                    )
                    Text(
                        text = inscription?.period ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF8B6914)
                    )
                }
            }

            // Kannada Section
            inscription?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E7)),
                    border = BorderStroke(1.dp, Color(0xFFE8B84B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ಕನ್ನಡ ಅನುವಾದ",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF8B6914)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = it.translationKannada,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Info Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "📍 ${inscription?.location}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "🏛 ${inscription?.dynasty} Dynasty", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(text = "📅 ${inscription?.period}", style = MaterialTheme.typography.bodySmall)
                }
            }

            // AI Story Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "✨ AI-Generated Story",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF8B6914)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFF8B6914))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Generating historical story...", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    } else {
                        Text(
                            text = aiStory.ifBlank { inscription?.description ?: "" },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
