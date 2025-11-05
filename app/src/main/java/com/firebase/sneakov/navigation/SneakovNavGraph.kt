package com.firebase.sneakov.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.firebase.sneakov.ui.screen.AuthScreen
import com.firebase.sneakov.ui.screen.DetailScreen
import com.firebase.sneakov.ui.screen.HomeScreen
import com.firebase.sneakov.ui.screen.OnboardingScreen
import com.firebase.sneakov.ui.screen.ProfileScreen
import com.firebase.sneakov.ui.screen.ResetPasswordScreen
import com.firebase.sneakov.ui.screen.SearchScreen
import com.firebase.sneakov.ui.screen.WishlistScreen

@Composable
fun SneakovNavGraph(navController: NavHostController, modifier: Modifier) {

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                goToResetPasswordScreen = {
                    navController.navigate(Screen.ResetPassword.route)
                })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { product ->
                    navController.navigate(Screen.Detail.createRoute(product.id))
                },
                goToSearchScreen = { keyword ->
                    navController.navigate(Screen.Search.createRoute(keyword = keyword))
                },
                goToSearchScreenWithBrand = { brand ->
                    navController.navigate(Screen.Search.createRoute(brand = brand.name))
                },
                goToSearchScreenWithLatest = {
                    navController.navigate(Screen.Search.createRoute(latest = true))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailScreen(id = id)
        }

        composable(route = Screen.Wishlist.route) {
            WishlistScreen(
                onProductClick = { product ->
                    navController.navigate(Screen.Detail.createRoute(product.id))
                }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument("keyword") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("brand") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("latest") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
            val brand = backStackEntry.arguments?.getString("brand")?.ifBlank { null }
            val latest = backStackEntry.arguments?.getBoolean("latest") ?: false

            SearchScreen(
                keyword = keyword,
                brand = brand,
                latest = latest,
                onProductClick = { product ->
                    navController.navigate(Screen.Detail.createRoute(product.id))
                }
            )
        }
        composable(route = Screen.ResetPassword.route) {
            ResetPasswordScreen()
        }
    }
}