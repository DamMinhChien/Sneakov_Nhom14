package com.firebase.sneakov.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Order
import java.sql.RowId
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun OrderItemView(
    order: Order,
    onViewDetail: (String) -> Unit,
    onMarkAsShippedClick: (String) -> Unit,
    onCancelOrderClick: (String) -> Unit
) {
    // định dạng ngày tháng
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val orderedAtDate = order.orderedAt?.toDate()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã đơn: ${order.orderId.take(6).uppercase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status.replaceFirstChar {
                        if(it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                    },
                    color = when(order.status) {
                        "pending" -> Color(0xFFFFA500) // Orange
                        "delivered" -> Color(0xFF4CAF50) // Green
                        "cancelled" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if(orderedAtDate != null) {
                Text("Ngày đặt: ${dateFormatter.format(orderedAtDate)}")
            }
            val totalQuantity = order.products.sumOf { it.quantity }
            Text("Tổng số lượng: $totalQuantity")
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                OutlinedButton(
                    onClick = { onViewDetail(order.orderId)},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Xem chi tiết")
                }
                // Chỉ hiển thị các nút hành động nếu đơn hàng đang ở trạng thái "pending"
                if (order.status == "pending") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nút "Hủy đơn"
                        Button(
                            onClick = { onCancelOrderClick(order.orderId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Hủy đơn")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Nút "Đã nhận"
                        Button(onClick = { onMarkAsShippedClick(order.orderId) }, modifier = Modifier.weight(1f)) {
                            Text("Đã nhận")
                        }
                    }
                }
            }
        }
    }

}