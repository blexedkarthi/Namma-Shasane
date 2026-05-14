package com.nammashasane.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nammashasane.app.data.Inscription
import com.nammashasane.app.navigation.Screen
import com.nammashasane.app.viewmodel.InscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInscriptionScreen(
    navController: NavController,
    viewModel: InscriptionViewModel
) {
    var title by remember { mutableStateOf("") }
    var dynasty by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latStr by remember { mutableStateOf("") }
    var lngStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var translationKannada by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Inscription") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Tag a New Shasane", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Add historical records found in your vicinity to the public map.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Inscription Title *") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dynasty,
                onValueChange = { dynasty = it },
                label = { Text("Dynasty (e.g. Chalukya)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = period,
                onValueChange = { period = it },
                label = { Text("Period / Year (e.g. 634 CE)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location (Village / District)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = latStr,
                    onValueChange = { latStr = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lngStr,
                    onValueChange = { lngStr = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (English)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = translationKannada,
                onValueChange = { translationKannada = it },
                label = { Text("ಕನ್ನಡ ವಿವರಣೆ (Kannada Description)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val inscription = Inscription(
                            title = title,
                            dynasty = dynasty.ifBlank { "Unknown" },
                            period = period.ifBlank { "Unknown" },
                            location = location.ifBlank { "Karnataka" },
                            latitude = latStr.toDoubleOrNull() ?: 15.3173,
                            longitude = lngStr.toDoubleOrNull() ?: 75.7139,
                            description = description,
                            translationKannada = translationKannada,
                            isUserAdded = true
                        )
                        viewModel.addInscription(inscription)
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Map.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Save to Heritage Map")
            }
        }
    }
}
