package com.firebase.sneakov.navigation

import java.net.URLEncoder

sealed class Screen(val route: String){
    object Onboarding: Screen("onboarding")
    object Auth: Screen("auth")
    object Home: Screen("home")
    object Search: Screen("search")
    object SearchResult: Screen("searchResult/{keyword}/{sortField}/{sortDirection}/{page}/{pageSize}"){
        fun createRoute(
            keyword: String = "",
            sortField: String = "name",
            sortDirection: String = "ASC",
            page: Int = 1,
            pageSize: Int = 20
        ): String {
            // encode keyword để tránh lỗi khi có dấu cách, ký tự đặc biệt
            val encodedKeyword = URLEncoder.encode(keyword, "UTF-8")
            return "searchResult/$encodedKeyword/$sortField/$sortDirection/$page/$pageSize"
        }
    }
    object ProductDetail: Screen("detail/{id}"){
        fun createRoute(id: String) = "detail/$id"
    }
}
