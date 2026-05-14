package com.nammashasane.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nammashasane.app.viewmodel.InscriptionViewModel
import com.nammashasane.app.ui.screens.*

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Camera : Screen("camera")
    object Story : Screen("story/{inscriptionId}") {
        fun createRoute(id: Int) = "story/$id"
    }
    object Alert : Screen("alert")
    object AddInscription : Screen("add_inscription")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: InscriptionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Map.route
    ) {
        composable(Screen.Map.route) {
            MapScreen(navController, viewModel)
        }
        composable(Screen.Camera.route) {
            CameraScreen(navController, viewModel)
        }
        composable(Screen.Story.route) { backStack ->
            val id = backStack.arguments?.getString("inscriptionId")?.toIntOrNull()
            StoryScreen(id, navController, viewModel)
        }
        composable(Screen.Alert.route) {
            AlertScreen(navController, viewModel)
        }
        composable(Screen.AddInscription.route) {
            AddInscriptionScreen(navController, viewModel)
        }
    }
}
