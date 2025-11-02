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

@Composable
fun SneakovNavGraph(navController: NavHostController, modifier: Modifier) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
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
            AuthScreen(onNavigateToHome = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                navigateToSearchScreen = {
                    navController.navigate(Screen.Search.route)
                },
                onProductClick = { product ->
                    navController.navigate(Screen.Detail.createRoute(product.id))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id"){type = NavType.StringType})
        ){ backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailScreen(id = id)
        }

//        composable(Screen.Search.route) {
//            SearchScreen(onSearchSubmit = { keyword ->
//                navController.navigate(Screen.SearchResult.createRoute(keyword))
//            })
//        }

//        composable(
//            route = Screen.SearchResult.route,
//            arguments = listOf(
//                navArgument("keyword") { type = NavType.StringType },
//                navArgument("sortField") { type = NavType.StringType },
//                navArgument("sortDirection") { type = NavType.StringType },
//                navArgument("page") { type = NavType.IntType },
//                navArgument("pageSize") { type = NavType.IntType })
//        ) { backStackEntry ->
//            val keyword = backStackEntry.arguments?.getString("keyword") ?: ""
//            val sortField = backStackEntry.arguments?.getString("sortField") ?: "created_at"
//            val sortDirection = backStackEntry.arguments?.getString("sortDirection") ?: "DESC"
//            val page = backStackEntry.arguments?.getInt("page") ?: 1
//            val pageSize = backStackEntry.arguments?.getInt("pageSize") ?: 20
//
//            SearchResultScreen(
//                keyword = keyword,
//                sortField = sortField,
//                sortDirection = sortDirection,
//                page = page,
//                pageSize = pageSize,
//                onProductClick = { product ->
//                    // TODO: Điều hướng sang màn chi tiết sản phẩm
//                }
//            )
//        }
//


    }
}