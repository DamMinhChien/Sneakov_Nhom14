package com.firebase.sneakov.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.navigation.SneakovNavGraph
import com.firebase.sneakov.ui.theme.SneakovTheme

@Composable
fun SneakovApp() {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Danh sách các màn không hiển thị top/bottom bar
    val noTopBarScreens = listOf(Screen.Onboarding.route, Screen.Auth.route, Screen.Cart.route)
    val noBottomBarScreens = listOf(Screen.Onboarding.route,  Screen.Auth.route, Screen.Cart.route)

    SneakovTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (currentRoute !in noTopBarScreens) {
                    TopBar()
                }
            },
            bottomBar = {
                if (currentRoute !in noBottomBarScreens) {
                    BottomBar()
                    //BottomBar(navController)
                }
            }
            ) { innerPadding ->
            SneakovNavGraph(navController = navController, modifier = Modifier.fillMaxSize().padding(innerPadding))
        }
    }
}

@Preview
@Composable
fun SneakovAppPreview() {
    SneakovTheme{
        SneakovApp()
    }
}