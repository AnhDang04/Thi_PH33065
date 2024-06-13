package com.example.thi_ph33065



import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thi_ph33065.screen.HomeScreen
import com.example.thi_ph33065.screen.WelcomeScreen

enum class ROUTE_SCREEN_NAME {
    WELCOME,
    HOME
}

@Composable
fun AppNavHost (navController : NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_SCREEN_NAME.WELCOME.name
    ) {
        composable(ROUTE_SCREEN_NAME.WELCOME.name) {
            WelcomeScreen(navController)
        }
        composable(ROUTE_SCREEN_NAME.HOME.name) {
            HomeScreen()

        }
    }
}