package com.firebase.sneakov.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.ui.compose.SurfaceIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    // 🏷️ Map route → tiêu đề tiếng Việt
    val routeTitleMap = mapOf(
        Screen.Home.route to "Sneakov",
        Screen.Search.route to "Tìm kiếm",
        Screen.Wishlist.route to "Yêu thích",
        Screen.Cart.route to "Giỏ hàng",
        Screen.Order.route to "Thanh toán",
        "settings" to "Tài khoản",
        Screen.Detail.route to "Chi tiết sản phẩm",
        Screen.Profile.route to "Cá nhân"
    )
    val title = routeTitleMap[currentRoute] ?: currentRoute

    // 👇 Mặc định: tất cả đều có nút Back
    var navigationIcon: @Composable (() -> Unit) = {
        SurfaceIcon(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = { navController.popBackStack() }
        )
    }

    // 👇 Action icon có thể null (không hiển thị)
    var actionIcon: (@Composable () -> Unit)? = null

    when (currentRoute) {
        Screen.Home.route -> {
            navigationIcon = {
                SurfaceIcon(
                    icon = Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    onClick = { /* TODO: mở menu hoặc drawer */ }
                )
            }
            actionIcon = {
                SurfaceIcon(
                    icon = Icons.Outlined.Search,
                    contentDescription = "Search",
                    onClick = { navController.navigate("search") }
                )
            }
        }

        //  Màn yêu thích
        Screen.Wishlist.route -> {
            actionIcon = {
                SurfaceIcon(
                    icon = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart",
                    onClick = { navController.navigate("cart") }
                )
            }
        }

        // 🛒 Màn chi tiết sản phẩm
        Screen.Detail.route -> {
            actionIcon = {
                SurfaceIcon(
                    icon = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    onClick = {
                        navController.navigate(Screen.Wishlist.route)
                    }
                )
            }
        }


        // Các màn khác (search, settings, cart...) sẽ chỉ có nút back, không có action
    }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = if(currentRoute == Screen.Home.route) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = { navigationIcon() },
        actions = {
            actionIcon?.invoke()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}