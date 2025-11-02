package com.firebase.sneakov.ui.compose

import android.R.attr.fontWeight
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.screen.DetailScreen
import com.firebase.sneakov.ui.theme.SneakovTheme
import com.firebase.sneakov.utils.formatMoney
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.regular.Heart

@Composable
fun ProductDetailContent(product: Product) {
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull()) }
    var selectedSize by remember {
        mutableStateOf(
            product.variants
                .filter { it.color == product.colors.firstOrNull()?.name }
                .minByOrNull { it.size }?.size
        )
    }
    var expandedDescription by remember { mutableStateOf(false) }

    val selectedImages = selectedColor?.images ?: emptyList()
    val variantsOfColor = remember(selectedColor) {
        product.variants.filter { it.color == selectedColor?.name }
    }
    val selectedVariant = variantsOfColor.firstOrNull { it.size == selectedSize }
    val priceFormatted = selectedVariant?.price?.formatMoney() ?: "0"
    val stock = selectedVariant?.stock ?: 0
    val allSizes = product.variants.map { it.size }.distinct().sorted()

    // ✅ Bọc toàn bộ trong Box để cố định phần footer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // 🔹 Phần nội dung có thể cuộn
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // chừa chỗ cho thanh cố định
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Thương hiệu:  ${product.brand}",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Giá: ${priceFormatted}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF007BFF)
                )
                Text(
                    text = if (stock > 0) "Tồn kho: $stock" else "Hết hàng!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (stock > 0)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            if (selectedImages.isNotEmpty()) {
                ImageSlider(selectedImages)
            } else if (product.thumbnail != null) {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))

            Text("Màu sắc:", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(product.colors.size) { index ->
                    val colorItem = product.colors[index]
                    val firstImage = colorItem.images.firstOrNull()
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (selectedColor == colorItem) 2.dp else 1.dp,
                                color = if (selectedColor == colorItem) Color.Blue else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedColor = colorItem
                                selectedSize = product.variants
                                    .filter { it.color == colorItem.name }
                                    .minByOrNull { it.size }?.size
                            }
                    ) {
                        AsyncImage(
                            model = firstImage,
                            contentDescription = colorItem.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (allSizes.isNotEmpty()) {
                Text("Kích thước:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(allSizes.size) { index ->
                        val sizeValue = allSizes[index]
                        val variantForSize = variantsOfColor.firstOrNull { it.size == sizeValue }
                        val isAvailableForColor = variantForSize != null
                        val isSelected = selectedSize == sizeValue

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = when {
                                        isSelected -> Color.Blue
                                        isAvailableForColor -> Color.Gray
                                        else -> Color.LightGray
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = isAvailableForColor) {
                                    selectedSize = sizeValue
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "EU $sizeValue",
                                color = if (isAvailableForColor) Color.Unspecified else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (!product.description.isNullOrBlank()) {
                val shortDesc = if (product.description.length > 120 && !expandedDescription)
                    product.description.take(120) + "..."
                else
                    product.description

                Text(
                    text = shortDesc,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (product.description.length > 120) {
                    Text(
                        text = if (expandedDescription) "Ẩn bớt" else "Đọc thêm",
                        color = Color.Blue,
                        modifier = Modifier.clickable { expandedDescription = !expandedDescription }
                    )
                }
            }

            Spacer(Modifier.height(100.dp)) // chừa thêm cho footer
        }

        // 🔹 Phần nút cố định dưới
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { /* TODO: Yêu thích */ },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(0.2f),
                        shape = CircleShape
                    )
                    .padding(6.dp)
                    .size(38.dp)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Regular.Heart,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(24.dp)
                )
            }

            OutlinedButton(
                onClick = { /* TODO: Giỏ hàng */ },
                modifier = Modifier.weight(1f),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (stock > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = selectedVariant != null && stock > 0
            ) {
                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = "ShoppingCart",
                    tint = if (stock > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (stock > 0) "Thêm giỏ hàng" else "Hết hàng!",
                    color = if (stock > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
@Preview(showSystemUi = true, showBackground = true)
fun HomePreViewNice() {
    SneakovTheme {
        DetailScreen(
            viewModel = viewModel() ,
            id = ""
        )
    }
}
