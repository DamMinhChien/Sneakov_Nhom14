package com.firebase.sneakov.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.navigation.SneakovNavGraph
import com.firebase.sneakov.ui.theme.SneakovTheme
import com.firebase.sneakov.utils.Prefs
import com.firebase.sneakov.utils.loadPrefsBoolean
import com.firebase.sneakov.utils.savePrefsBoolean
import kotlinx.coroutines.launch

@Composable
fun SneakovApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    var darkTheme by remember {
        mutableStateOf(loadPrefsBoolean(context, Prefs.DARK_MODE))
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Danh sách các màn không hiển thị top/bottom bar
    val noTopBarScreens =
        listOf(
            Screen.Onboarding.route,
            Screen.Auth.route,
            Screen.ResetPassword.route,
            Screen.Model3D.route
        )
    val noBottomBarScreens = listOf(
        Screen.Onboarding.route,
        Screen.Auth.route,
        Screen.Detail.route,
        Screen.Search.route,
        Screen.ResetPassword.route,
        Screen.Model3D.route
    )

    SneakovTheme(darkTheme = darkTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                DrawerContent(
                    navController = navController,
                    darkTheme = darkTheme,
                    onDarkModeToggle = {
                        darkTheme = !darkTheme
                        savePrefsBoolean(context, Prefs.DARK_MODE, darkTheme)
                    },
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (currentRoute !in noTopBarScreens) {
                        TopBar(
                            navController,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            })
                    }
                },
                bottomBar = {
                    if (currentRoute !in noBottomBarScreens) {
                        BottomBar(navController)
                    }
                }
            ) { innerPadding ->
                SneakovNavGraph(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Preview
@Composable
fun SneakovAppPreview() {
    SneakovTheme {
        SneakovApp()
    }
}