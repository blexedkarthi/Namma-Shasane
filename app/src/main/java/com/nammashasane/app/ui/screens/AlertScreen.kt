package com.nammashasane.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nammashasane.app.viewmodel.InscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    navController: NavController,
    viewModel: InscriptionViewModel
) {
    val inscriptions by viewModel.inscriptions.collectAsState()
    var alertSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preservation Alerts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF8B2500), // AncientRed
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Warning header
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF8B2500))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Report a Damaged Inscription", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "Select an inscription and submit a report to the Archaeological Survey of India.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (alertSent) {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text(
                        text = "✅ Alert submitted! ASI has been notified.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(inscriptions) { inscription ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (inscription.isDamaged) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = inscription.title, style = MaterialTheme.typography.titleSmall)
                                Text(text = inscription.location, style = MaterialTheme.typography.bodySmall)
                                if (inscription.isDamaged) {
                                    Text(
                                        text = "⚠️ Reported as damaged",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF8B2500)
                                    )
                                }
                            }
                            
                            if (!inscription.isDamaged) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.markAsDamaged(inscription)
                                        alertSent = true
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8B2500))
                                ) {
                                    Text("Report")
                                }
                            } else {
                                Text(text = "Reported", style = MaterialTheme.typography.labelSmall, color = Color(0xFF8B2500))
                            }
                        }
                    }
                }
            }
        }
    }
}
