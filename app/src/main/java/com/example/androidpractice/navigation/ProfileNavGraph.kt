package com.example.androidpractice.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidpractice.ui.profile.EditProfileScreen
import com.example.androidpractice.ui.profile.ProfileScreen
import com.example.androidpractice.ui.profile.ProfileViewModel

@Composable
fun ProfileNavGraph(navController: NavHostController) {
    // Единый ViewModel для обоих экранов — чтобы редактирование и просмотр
    // работали с одними данными без лишних перезагрузок
    val viewModel: ProfileViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "profile"
    ) {
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                onEdit = { navController.navigate("profile_edit") }
            )
        }
        composable("profile_edit") {
            EditProfileScreen(
                viewModel = viewModel,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
