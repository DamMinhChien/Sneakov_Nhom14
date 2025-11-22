package com.firebase.sneakov.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.firebase.sneakov.ui.screen.AuthScreen
import com.firebase.sneakov.ui.screen.CartScreen
import com.firebase.sneakov.ui.screen.CheckoutScreen
import com.firebase.sneakov.ui.screen.DetailScreen
import com.firebase.sneakov.ui.screen.HomeScreen
import com.firebase.sneakov.ui.screen.Model3DScreen
import com.firebase.sneakov.ui.screen.NotificationScreen
import com.firebase.sneakov.ui.screen.OnboardingScreen
import com.firebase.sneakov.ui.screen.OrderDetailScreen
import com.firebase.sneakov.ui.screen.OrderScreen
import com.firebase.sneakov.ui.screen.ProfileScreen
import com.firebase.sneakov.ui.screen.ResetPasswordScreen
import com.firebase.sneakov.ui.screen.SearchScreen
import com.firebase.sneakov.ui.screen.WishlistScreen
import com.firebase.sneakov.utils.isOnboardingSeen
import com.firebase.sneakov.utils.setOnboardingSeen
import com.firebase.sneakov.viewmodel.NotificationViewModel
import com.firebase.sneakov.viewmodel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

object Graph {
    const val CHECKOUT = "checkout_graph"
}

@Composable
fun SneakovNavGraph(navController: NavHostController, modifier: Modifier) {
    val durationTime = 350
    val fadeTime = 300
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = when {
            !isOnboardingSeen(context) -> Screen.Onboarding.route
            FirebaseAuth.getInstance().currentUser != null -> Screen.Home.route
            else -> Screen.Auth.route
        },
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                SlideDirection.End,
                animationSpec = tween(durationTime, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(fadeTime))
        },
        exitTransition = {
            slideOutOfContainer(
                SlideDirection.Start,
                animationSpec = tween(durationTime, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(fadeTime))
        },
        popEnterTransition = {
            slideIntoContainer(
                SlideDirection.Start,
                animationSpec = tween(durationTime, easing = FastOutSlowInEasing)
            ) + fadeIn(tween(fadeTime))
        },
        popExitTransition = {
            slideOutOfContainer(
                SlideDirection.End,
                animationSpec = tween(durationTime, easing = FastOutSlowInEasing)
            ) + fadeOut(tween(fadeTime))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen {
                setOnboardingSeen(context, true)
                navController.navigate(Screen.Auth.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            }
        }

        composable(Screen.Auth.route, enterTransition = { fadeIn(tween(durationTime)) }) {
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

        composable(Screen.Home.route, enterTransition = { fadeIn(tween(durationTime)) }) {
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
            enterTransition = {
                slideIntoContainer(
                    SlideDirection.Up,
                    tween(durationTime, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(fadeTime))
            },

                    exitTransition = {
                slideOutOfContainer(
                    SlideDirection.Down,
                    tween(durationTime, easing = FastOutSlowInEasing)
                ) + fadeOut(tween(fadeTime))
            },
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailScreen(
                id = id,
                view3DModel = {
                    navController.navigate(Screen.Model3D.createRoute(it))
                }
            )
        }

        composable(
            route = Screen.Model3D.route,
            arguments = listOf(navArgument("glbUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("glbUrl") ?: ""
            val decodedUrl = Uri.decode(encodedUrl)
            Model3DScreen(
                glbUrl = decodedUrl,
                onBackClick = {
                    navController.popBackStack()
                }
            )
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

        navigation(
            startDestination = Screen.Cart.route,
            route = Graph.CHECKOUT
        ) {
            composable(Screen.Cart.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Graph.CHECKOUT)
                }
                val orderViewModel: OrderViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                CartScreen(
                    orderViewModel = orderViewModel,
                    onCheckout = { navController.navigate(Screen.Order.route) },
                    onBack = { navController.popBackStack() }
                )

            }
            composable(Screen.Order.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Graph.CHECKOUT)
                }
                val orderViewModel: OrderViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val notificationViewModel: NotificationViewModel = koinViewModel()
                CheckoutScreen(
                    orderViewModel = orderViewModel,
                    onBack = { navController.popBackStack() },
                    onCheckoutSuccess = { orderId ->
                        notificationViewModel.createOrderNotification(orderId)

                        navController.navigate(Screen.Home.route) {
                            popUpTo(Graph.CHECKOUT) {
                                inclusive = true
                            }
                        }

                    }
                )

            }

        }
        composable(Screen.Notification.route) {
            val viewModel: NotificationViewModel = koinViewModel()
            NotificationScreen(viewModel = viewModel)
        }
        composable(Screen.OrderHistory.route) {
            OrderScreen(navController = navController)
        }
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") {
                type = NavType.StringType
            })
        ) {
            OrderDetailScreen()
        }
    }
}