package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.firebase.sneakov.data.mapper.toShippingAddress
import com.firebase.sneakov.data.model.Address
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.model.ShippingAddress
import com.firebase.sneakov.utils.formatMoney
import com.firebase.sneakov.viewmodel.CheckoutResultState // Sửa import
import com.firebase.sneakov.viewmodel.OrderViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit = {},
    onCheckoutSuccess: (String) -> Unit = {},
    orderViewModel: OrderViewModel, // ViewModel được inject từ NavGraph
    userViewModel: UserViewModel = koinViewModel()
) {
    // BƯỚC 1: LẤY CÁC STATE MỚI TỪ VIEWMODEL
    val sessionState by orderViewModel.sessionState.collectAsState()
    val resultState by orderViewModel.checkoutState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()

    // LẤY DỮ LIỆU THANH TOÁN TỪ  VIEWMODEL
    val itemsToCheckout = sessionState.itemsToCheckout
    val subtotal = sessionState.subtotal
    val shippingFee = sessionState.shippingFee
    val total = sessionState.totalCost

    //Lấy thông tin user
    val currentUser = userState.data
    val email = currentUser?.email


    var shippingAddress by remember { mutableStateOf(ShippingAddress()) }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            shippingAddress = user.toShippingAddress()
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }


    var paymentMethod by remember { mutableStateOf("COD (Thanh toán khi nhận hàng)") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
    ) { padding ->
        if(userState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }else if(currentUser != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Phần nội dung cuộn
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //HIỂN THỊ DANH SÁCH SẢN PHẨM
                    CheckoutCard(title = "Sản phẩm (${itemsToCheckout.size})") {
                        itemsToCheckout.forEach { (cart, product) ->
                            ProductCheckoutRow(product = product, cart = cart)
                        }
                    }

                    // ==== Thông tin liên hệ ====
                    CheckoutCard(title = "Thông tin liên hệ") {
                        InfoRow(Icons.Default.Email, email ?: "Chưa có email", "Email")
                        InfoRow(Icons.Default.Phone, shippingAddress.phone.ifBlank { "Chưa có SĐT" }, "Số điện thoại")
                    }

                    // ==== Địa chỉ ====
                    CheckoutCard(title = "Địa chỉ giao hàng") {
                        if(shippingAddress.province.isBlank()) {
                            Text("Hãy cập nhật địa chỉ của bạn")
                        }else {
                            InfoRow(
                                Icons.Default.LocationOn,
                                "${shippingAddress.detail}, ${shippingAddress.commune}, ${shippingAddress.district}, ${shippingAddress.province}"
                            )
                        }
                        // TODO: Thêm nút "Thay đổi"
                    }

                    // ==== Phương thức thanh toán ====
                    CheckoutCard(title = "Phương thức thanh toán") {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = paymentMethod,
                                onValueChange = {},
                                label = { Text("Chọn phương thức") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf(
                                    "COD (Thanh toán khi nhận hàng)",
                                    "Thẻ tín dụng / Ghi nợ",
                                    "Ví ZaloPay",
                                    "Ví Momo"
                                ).forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            paymentMethod = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // BƯỚC 3: CẬP NHẬT TỔNG CHI PHÍ
                    CheckoutCard(title = "Tổng chi phí") {
                        CostRow2("Tạm tính", subtotal)
                        CostRow2("Phí giao hàng", shippingFee)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Tổng cộng", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                total.formatMoney(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                // Phần nút đặt hàng cố định ở dưới
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    // BƯỚC 4: CẬP NHẬT LỜI GỌI HÀM placeOrder
                    Button(
                        onClick = {
                            orderViewModel.placeOrder(shippingAddress, paymentMethod)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        // Vô hiệu hóa nút khi đang loading
                        enabled = resultState !is CheckoutResultState.Loading
                    ) {
                        if (resultState is CheckoutResultState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Đặt hàng", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            // BƯỚC 5: CẬP NHẬT LOGIC DIALOG
            when (val state = resultState) {
                is CheckoutResultState.Success -> {
                    SuccessDialog(orderId = state.orderId)
                    // Điều hướng sau một khoảng delay
                    LaunchedEffect(state.orderId) {
                        delay(2000) // Tăng delay để người dùng kịp đọc
                        onCheckoutSuccess(state.orderId)
                    }
                }
                is CheckoutResultState.Error -> {
                    var showErrorDialog by remember { mutableStateOf(true) }
                    if (showErrorDialog) {
                        ErrorDialog(message = state.message, onDismiss = {
                            orderViewModel.dismissError() // Reset trạng thái
                            showErrorDialog = false
                        })
                    }
                }
                else -> {} // Idle và Loading không cần hiển thị dialog từ đây
            }
        }
        }

}

// Thêm Composable mới để hiển thị sản phẩm
@Composable
fun ProductCheckoutRow(product: Product?, cart: Cart) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product?.thumbnail,
            contentDescription = product?.name,
            modifier = Modifier
                .size(56.dp)
                .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(product?.name ?: "Sản phẩm không xác định", fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text("SL: ${cart.quantity}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        val variant = product?.variants?.find { it.id == cart.variantId }
        Text((variant?.price ?: 0L).formatMoney(), fontWeight = FontWeight.Medium)
    }
}
@Composable
fun CheckoutCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, sub: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(label, fontWeight = FontWeight.Medium)
            if (sub != null)
                Text(sub, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CostRow2(label: String, amount: Long) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(amount.formatMoney(), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

// Cập nhật các Dialog để chuyên nghiệp hơn
@Composable
fun SuccessDialog(orderId: String) {
    Dialog(onDismissRequest = {}) {
        Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(56.dp))
                Text(
                    text = "Đặt hàng thành công!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Mã đơn hàng của bạn:\n$orderId",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        },
        title = { Text("Đã có lỗi xảy ra") },
        text = { Text(message) },
    )
}
