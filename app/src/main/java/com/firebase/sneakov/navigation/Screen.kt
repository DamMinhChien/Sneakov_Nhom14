package com.firebase.sneakov.navigation

import com.firebase.sneakov.viewmodel.ProductFilter
import java.net.URLEncoder

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Search : Screen("search?keyword={keyword}&brand={brand}&latest={latest}") {
        fun createRoute(keyword: String? = null, brand: String? = null, latest: Boolean = false): String {
            val params = buildList {
                if (!keyword.isNullOrBlank()) add("keyword=${URLEncoder.encode(keyword, "UTF-8")}")
                if (!brand.isNullOrBlank()) add("brand=${URLEncoder.encode(brand, "UTF-8")}")
                if (latest) add("latest=true")
            }
            return if (params.isEmpty()) "search" else "search?${params.joinToString("&")}"
        }
    }

    object Detail : Screen("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
    object Cart: Screen("cart")
    object Order: Screen("order")
    object Wishlist : Screen("wishlist")
    object Profile : Screen("profile")
    object ResetPassword : Screen("reset_password")
    object Model3D: Screen("model3d/{glbUrl}"){
        fun createRoute(glbUrl: String) = "model3d/$glbUrl"
    }
}
