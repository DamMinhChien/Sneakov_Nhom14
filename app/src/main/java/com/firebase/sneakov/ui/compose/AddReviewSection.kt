package com.firebase.sneakov.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddReviewSection(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Thêm đánh giá",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Hàng chọn số sao
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    val isSelected = starIndex <= rating

                    // Hiệu ứng đổi màu mượt
                    val starColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFFFFC107) else Color.LightGray,
                        label = "starColorAnim"
                    )

                    Icon(
                        imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Star $starIndex",
                        tint = starColor,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(4.dp)
                            .clickable { onRatingChange(starIndex) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Ô nhập nội dung đánh giá
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChange,
                label = { Text("Nhận xét của bạn") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🔹 Nút gửi đánh giá
            Button(
                onClick = onSubmit,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Gửi đánh giá")
            }
        }
    }
}
