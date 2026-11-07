package com.example.praktikum5_navigationcompose_245150200111040

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.praktikum5_navigationcompose_245150200111040.data.remote.RetrofitClient
import com.example.praktikum5_navigationcompose_245150200111040.data.remote.StudentRemoteDataSource
import com.example.praktikum5_navigationcompose_245150200111040.data.repository.DefaultStudentRepository
import com.example.praktikum5_navigationcompose_245150200111040.ui.detail.DetailViewModel
import com.example.praktikum5_navigationcompose_245150200111040.ui.home.HomeViewModel
import com.example.praktikum5_navigationcompose_245150200111040.ui.screen.AboutScreen
import com.example.praktikum5_navigationcompose_245150200111040.ui.screen.DetailScreen
import com.example.praktikum5_navigationcompose_245150200111040.ui.screen.HomeScreen
import com.example.praktikum5_navigationcompose_245150200111040.ui.screen.ProfileScreen

@Composable
fun NavigationLabApp() {
    val remoteDataSource = remember {
        StudentRemoteDataSource(RetrofitClient.apiService)
    }
    val repository = remember {
        DefaultStudentRepository(remoteDataSource)
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.factory(repository)
            )

            val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
            val searchQuery by homeViewModel.searchQuery.collectAsStateWithLifecycle()

            HomeScreen(
                uiState = uiState,
                searchQuery = searchQuery,
                onSearchQueryChange = homeViewModel::onSearchQueryChange,
                onStudentClick = { id ->
                    navController.navigate(Routes.detail(id))
                },
                onOpenProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onOpenAbout = {
                    navController.navigate(Routes.ABOUT)
                },
                onRetry = homeViewModel::retry
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1

            val viewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.factory(repository)
            )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(studentId) {
                viewModel.loadStudent(studentId)
            }

            DetailScreen(
                uiState = uiState,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
