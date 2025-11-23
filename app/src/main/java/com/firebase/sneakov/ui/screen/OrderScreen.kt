package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.navigation.Screen
import com.firebase.sneakov.ui.compose.OrderItemView
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.OrderViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderScreen(
    orderViewModel: OrderViewModel = koinViewModel(),
    navController: NavController
){
    //lấy state từ viewmodel
    val orders by orderViewModel.userOrders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.fetchUserOrders()
    }

    val tabs = listOf("Đã xác nhận", "Đã giao", "Đã huỷ")
    val paperState = rememberPagerState(pageCount = {tabs.size})
    val coroutineScope = rememberCoroutineScope()

    //phân loại danh sách dựa trên trạng thái đơn hàng
    val processingOrders = remember(orders) { orders.filter { it.status == "pending" } }
    val deliveredOrders = remember(orders) { orders.filter { it.status == "delivered" }}
    val canceledOrders = remember(orders) { orders.filter { it.status == "canceled" }}

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = paperState.currentPage
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = paperState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                paperState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }
            RefreshableLayout(
                isRefreshing = isLoading,
                onRefresh = { orderViewModel.fetchUserOrders()},
            ){
                HorizontalPager(
                    state = paperState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when(page) {
                        0 -> OrderList(orders = processingOrders, navController = navController)
                        1 -> OrderList(orders = deliveredOrders, navController = navController)
                        2 -> OrderList(orders = canceledOrders, navController = navController)
                    }
                }

            }
        }

    }
}

@Composable
fun OrderList(orders: List<Order>, navController: NavController) {
    val orderViewModel: OrderViewModel = koinViewModel()
    if(orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            Text("Không có đơn hàng nào")

        }
    }else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(orders) { order ->
                OrderItemView(
                    order = order,
                    onViewDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onMarkAsShippedClick = { orderId ->
                        orderViewModel.shipOrder(orderId)
                    },
                    onCancelOrderClick = { orderId ->
                        orderViewModel.cancelOrder(orderId)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.fillParentMaxHeight())
            }
        }
    }
}