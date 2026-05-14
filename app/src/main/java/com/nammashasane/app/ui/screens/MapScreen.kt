package com.nammashasane.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nammashasane.app.data.Inscription
import com.nammashasane.app.navigation.Screen
import com.nammashasane.app.viewmodel.InscriptionViewModel

@Composable
fun MapScreen(
    navController: NavController,
    viewModel: InscriptionViewModel
) {
    val inscriptions by viewModel.inscriptions.collectAsState()
    var selectedMarker by remember { mutableStateOf<Inscription?>(null) }
    
    val karnatakaCenter = LatLng(15.3173, 75.7139)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(karnatakaCenter, 7f)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            inscriptions.forEach { inscription ->
                Marker(
                    state = MarkerState(position = LatLng(inscription.latitude, inscription.longitude)),
                    title = inscription.title,
                    snippet = "${inscription.dynasty} · ${inscription.period}",
                    onClick = {
                        selectedMarker = inscription
                        false
                    }
                )
            }
        }
        
        // Marker Info Card
        selectedMarker?.let { inscription ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = inscription.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${inscription.dynasty} · ${inscription.period}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = inscription.location,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.selectInscription(inscription)
                                navController.navigate(Screen.Story.createRoute(inscription.id))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Read Story")
                        }
                        OutlinedButton(
                            onClick = { selectedMarker = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Close")
                        }
                    }
                }
            }
        }
        
        // Count Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "${inscriptions.size} Shasanas Found",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
