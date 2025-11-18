package com.firebase.sneakov.ui.app

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.firebase.sneakov.R
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.ui.compose.Dialog
import com.firebase.sneakov.viewmodel.AuthViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Heart
import compose.icons.fontawesomeicons.solid.Moon
import compose.icons.fontawesomeicons.solid.ShoppingCart
import compose.icons.fontawesomeicons.solid.SignOutAlt
import compose.icons.fontawesomeicons.solid.Truck
import compose.icons.fontawesomeicons.solid.User
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    navController: NavHostController,
    darkTheme: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    closeDrawer: () -> Unit,
    userViewModel: UserViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),

    ) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val drawerWidth = screenWidthDp * 4 / 5

    val userState by userViewModel.uiState.collectAsState()
    val user = userState.data

    var showConfirmLogout by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    ModalDrawerSheet(
        modifier = Modifier.width(drawerWidth)
    ) {
        user?.let {
            Column(modifier = Modifier.padding(24.dp)) {
                if(showConfirmLogout){
                    Dialog(
                        showDialog = showConfirmLogout,
                        title = "Xác nhận",
                        onDismiss = {
                            showConfirmLogout = false
                        },
                        onConfirm = {
                            showConfirmLogout = false
                            closeDrawer()
                            authViewModel.logout()
                        },
                        confirmLabel = "Đăng xuất",
                    ) {
                        Text("Bạn có chắc chắn muốn đăng xuất không?")
                    }
                }
                // --- Avatar ---
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = rememberAsyncImagePainter(model = it.avatar_url.ifBlank { R.drawable.men }),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                navController.navigate(Screen.Profile.route)
                                closeDrawer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            FontAwesomeIcons.Solid.Check,
                            contentDescription = "icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                Text(text = it.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = it.email, style = MaterialTheme.typography.titleSmall)

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Tài khoản
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.startDestinationId)
                            }
                            closeDrawer()
                        }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.User,
                        contentDescription = "Account",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tài khoản")
                }

                // Giỏ hàng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(Screen.Cart.route) {
                                popUpTo(navController.graph.startDestinationId)
                            }
                            closeDrawer()
                        }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ShoppingCart,
                        contentDescription = "Cart",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Giỏ hàng")
                }

                // Yêu thích
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(Screen.Wishlist.route) {
                                popUpTo(navController.graph.startDestinationId)
                            }
                            closeDrawer()
                        }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Heart,
                        contentDescription = "Wishlist",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yêu thích")
                }

                // Đơn hàng
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(Screen.OrderHistory.route) {
                                popUpTo(navController.graph.startDestinationId)
                            }
                            closeDrawer()
                        }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Truck,
                        contentDescription = "Order",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đơn hàng")
                }

                // Thông báo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(Screen.Notification.route) {
                                popUpTo(navController.graph.startDestinationId)
                            }
                            closeDrawer()
                        }


                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Bell,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Thông báo")
                }

                // Dark Mode Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Moon,
                        contentDescription = "Dark Mode",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chế độ tối")
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(
                        checked = darkTheme,
                        onCheckedChange = onDarkModeToggle
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Đăng xuất
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            showConfirmLogout = true
                        }
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.SignOutAlt,
                        contentDescription = "Logout",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất")
                }
            }
        }
    }
}
