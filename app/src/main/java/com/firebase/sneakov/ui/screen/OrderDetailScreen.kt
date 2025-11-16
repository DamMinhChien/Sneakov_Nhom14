package com.firebase.sneakov.ui.screen

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.data.model.OrderItem
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.OrderDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderDetailScreen(
    viewModel: OrderDetailViewModel = koinViewModel()
){

    val state by viewModel.state.collectAsState()
    val order = state.order
    val productsMap = state.products
    val isLoading = state.isLoading


    Scaffold { paddingValues ->
        RefreshableLayout(
            isRefreshing = isLoading,
            onRefresh = { viewModel.refreshOrder()},
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Nếu không đang tải và order vẫn là null -> Lỗi
            if (!isLoading && order == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không tìm thấy đơn hàng")
                }
            }
            // Nếu có order -> Hiển thị
            else if (order != null) {
                OrderDetailContent(
                    order = order!!,
                    productsMap = productsMap,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun OrderDetailContent(order: Order, productsMap: Map<String, Product>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Mã đơn hàng", style = MaterialTheme.typography.titleMedium)
                Text(
                    "${order.orderId.uppercase()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item {
            Text("Sản phẩm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(order.products) { orderItem ->
            val product = productsMap[orderItem.productId]
            ProductInDetailView(item = orderItem, product = product)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Địa chỉ giao hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(order.shippingAddress?.name ?: "N/A")
                    Text(order.shippingAddress?.phone ?: "N/A")
                    Text(
                        "${order.shippingAddress?.detail}, ${order.shippingAddress?.commune}, ${order.shippingAddress?.district}, ${order.shippingAddress?.province}"
                    )
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chi tiết thanh toán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    val subtotal = order.products.sumOf { it.price * it.quantity }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng tiền hàng")
                        Text("${String.format("%,d", subtotal)}₫")
                    }
                    // Bạn có thể thêm phí ship nếu có
                    // Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    //     Text("Phí vận chuyển")
                    //     Text("30,000₫")
                    // }
                    Divider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng thanh toán", fontWeight = FontWeight.Bold)
                        Text("${String.format("%,d", subtotal)}₫", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

}

@Composable
private fun ProductInDetailView(item: OrderItem, product: Product?) {
    val variant = product?.variants?.find { it.id == item.variantId }
    val productColor = product?.colors?.find { it.name == variant?.color }
    val imageUrl = productColor?.images?.getOrNull(0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = product?.name ?: "Ảnh",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = product?.name ?: "Sản phẩm không xác định",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
            if (variant != null) {
                Text("Phân loại", style = MaterialTheme.typography.bodySmall)
                Text("Màu: ${variant.color}", style = MaterialTheme.typography.bodySmall)
                Text("Size: ${variant.size}" , style = MaterialTheme.typography.bodySmall)

            }
            Text("Số lượng: ${item.quantity}")
        }
        Text("${String.format("%,d", item.price)}₫")
    }
}
