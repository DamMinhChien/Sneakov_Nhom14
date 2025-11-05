package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.ui.compose.CartItemRow
import com.firebase.sneakov.utils.formatMoney
import com.firebase.sneakov.viewmodel.CartViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import com.firebase.sneakov.ui.theme.SneakovTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = koinViewModel(),
    onCheckout: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val cartItem by viewModel.cartItems.collectAsState()
    val subtotal = cartItem.sumOf { (cart, product) ->
        (product?.variants?.find { it.id == cart.variantId }?.price ?: 0L) * cart.quantity
    }
    val deliveryFee = 30000L // Phí vận chuyển cố định, ví dụ 30,000đ
    val totalCost = subtotal + deliveryFee

    LaunchedEffect(Unit) {
        viewModel.loadCart("user_001") // user giả định
    }
    Scaffold(
        // ... (bên trong Scaffold)
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets),
                title = {
                    Text(
                        text = "Giỏ hàng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (cartItem.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        // 1. Hàng Subtotal
                        CostRow(label = "Tổng phụ", amount = subtotal)

                        // 2. Hàng Delivery
                        CostRow(label = "Vận chuyển", amount = deliveryFee)

                        // 3. Đường kẻ mờ
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        // 4. Hàng Total Cost
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tổng chi phí",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = totalCost.formatMoney(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5. Nút Thanh toán
                        Button(
                            onClick = onCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Đặt hàng", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }

    ) { paddingValues ->
        if (cartItem.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // Thêm paddingValues
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng trống")
            }
        }else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp, 0.dp),
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItem) { (cart, product) ->
                    CartItemRow(
                        cart = cart,
                        product = product,
                        onPlus = { viewModel.updateQuantity(cart.id, cart.quantity +1, cart.userId) },
                        onMinus = { viewModel.updateQuantity(cart.id, cart.quantity -1, cart.userId)  },
                        onRemove = { viewModel.removeFromCart(cart.id, cart.userId) }
                    )
                }
            }
        }
    }
}

// Hàm Composable trợ giúp để hiển thị các dòng chi phí phụ
@Composable
fun CostRow(label: String, amount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Khoảng cách giữa các dòng nhỏ hơn
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray // Màu chữ hơi mờ như trong ảnh
        )
        Text(
            text = amount.formatMoney(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview()
@Composable
fun CartPreview() {
    SneakovTheme {
        CartScreen()
    }
}
