package com.firebase.sneakov.ui.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.utils.BottomNavItem

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Trang chủ", Icons.Outlined.Home),
        BottomNavItem(Screen.Wishlist.route, "Yêu thích", Icons.Outlined.FavoriteBorder),
        BottomNavItem(Screen.Cart.route, "Giỏ hàng", Icons.Outlined.ShoppingCart),
        BottomNavItem(Screen.Home.route, "Thông báo", Icons.Outlined.Notifications),
        BottomNavItem(Screen.Profile.route, "Tài khoản", Icons.Outlined.Person)
    )

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                val selected = currentRoute == item.route
                val scale by animateFloatAsState(if (selected) 1.3f else 1f, label = "")

                NavigationBarItem(
                    icon = {
                        // Có badge ví dụ cho tab Wishlist
                        if (item.route == Screen.Wishlist.route) {
                            BadgedBox(
                                badge = {
                                    if (selected) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ) { Text("3") }
                                    }
                                }
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .graphicsLayer(
                                            scaleX = scale,
                                            scaleY = scale
                                        )
                                )
                            }
                        } else {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale
                                    )
                            )
                        }
                    },
                    label = {
                        if (selected){
                            Text(item.label,style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                )

            }
        }
    }

}