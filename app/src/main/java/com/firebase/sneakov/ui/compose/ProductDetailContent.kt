//package com.magento.sneakov.presentation.ui.compose
//
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.firebase.sneakov.ui.compose.ImageSlider
//import com.magento.sneakov.domain.model.ProductDetail
//
//@Composable
//fun ProductDetailContent(detail: ProductDetail) {
//    var selectedColor by remember { mutableStateOf(detail.colorGroups.keys.firstOrNull()) }
//    var expandedDescription by remember { mutableStateOf(false) }
//
//    val selectedImages = detail.colorGroups[selectedColor]  // danh sách variant theo màu
//        ?.flatMap { it.imageUrls }
//        ?.distinct()
//        ?: emptyList()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(16.dp)
//    ) {
//
//        // 🔹 1. Tên sản phẩm
//        Text(
//            text = detail.name,
//            style = MaterialTheme.typography.titleLarge,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        // 🔹 2. Giá
//        detail.priceRange?.let { (min, max) ->
//            Text(
//                text = if (min == max)
//                    "Giá: ${min.toInt()}₫"
//                else
//                    "Giá: ${min.toInt()}₫ - ${max.toInt()}₫",
//                style = MaterialTheme.typography.titleMedium,
//                color = Color(0xFF007BFF)
//            )
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // 🔹 3. Slider ảnh
//        if (selectedImages.isNotEmpty()) {
//            val filtered = selectedImages
//            ImageSlider(filtered)
//        }
//
//        Spacer(Modifier.height(8.dp))
//
//        // 🔹 4. LazyRow chọn màu
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(detail.colorGroups.keys.toList()) { color ->
//                val variants = detail.colorGroups[color]
//                val firstImage = variants?.firstOrNull()?.imageUrls?.firstOrNull()
//                val imageUrl = (firstImage ?: "")
//                Box(
//                    modifier = Modifier
//                        .size(64.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .border(
//                            width = if (selectedColor == color) 2.dp else 1.dp,
//                            color = if (selectedColor == color) Color.Blue else Color.Gray,
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .clickable { selectedColor = color }
//                ) {
//                    AsyncImage(
//                        model = imageUrl,
//                        contentDescription = color,
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
//                }
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // 🔹 5. Mô tả ngắn + nút "Đọc thêm"
//        if (expandedDescription) detail.description else detail.shortDescription?.let {
//            Text(
//                text = it,
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//        if (detail.description != null && detail.shortDescription != null){
//            if (detail.description.length > detail.shortDescription.length) {
//                Text(
//                    text = if (expandedDescription) "Ẩn bớt" else "Đọc thêm",
//                    color = Color.Blue,
//                    modifier = Modifier.clickable { expandedDescription = !expandedDescription }
//                )
//            }
//        }
//
//
//        Spacer(Modifier.height(24.dp))
//
//        // 🔹 6. Nút hành động
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Button(
//                onClick = { /* TODO: Thêm yêu thích */ },
//                modifier = Modifier.weight(1f)
//            ) {
//                Text("❤ Yêu thích")
//            }
//
//            Button(
//                onClick = { /* TODO: Thêm giỏ hàng */ },
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF))
//            ) {
//                Text("🛒 Thêm giỏ hàng", color = Color.White)
//            }
//        }
//    }
//}
