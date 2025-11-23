package com.firebase.sneakov.ui.compose

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.utils.formatMoney
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Minus
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Trash
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CartItemRow(
    cart: Cart,
    product: Product?,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    onRemove: () -> Unit,
    isSelected: Boolean,
    onItemSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val maxReveal = 140f
    val threshold = maxReveal * 0.15f

    val variant = product?.variants?.find { it.id == cart.variantId }
    val productColor = product?.colors?.find { it.name == variant?.color }

    // Lấy URL ảnh đầu tiên từ ProductColor tìm được.
    // Nếu không tìm thấy, quay về dùng thumbnail của sản phẩm làm ảnh dự phòng.
    val imageUrl = productColor?.images?.firstOrNull() ?: product?.thumbnail

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(vertical = 4.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
        ) {
            // ==== Background ====
            Row(modifier = Modifier.matchParentSize()) {
                // Vuốt phải (hiện tăng/giảm)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF1976D2)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier.padding(start = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (offsetX.value > threshold) onPlus()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = "Tăng",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Text(
                            text = "${cart.quantity}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(10.dp, 0.dp)
                        )
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (offsetX.value > threshold) onMinus()
                                }
                            },
                            enabled = cart.quantity > 1
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Minus,
                                contentDescription = "Giảm",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Vuốt trái (hiện xoá)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFFF3B30)),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (offsetX.value < -threshold) {
                                    onRemove()
                                    offsetX.animateTo(0f, spring())
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xoá",
                            tint = Color.White
                        )
                    }
                }
            }

            // ==== Foreground (nội dung sản phẩm) ====
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onItemSelected(!isSelected)}
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val current = offsetX.value
                                    when {
                                        current > threshold -> offsetX.animateTo(maxReveal, spring())
                                        current < -threshold -> offsetX.animateTo(-maxReveal, spring())
                                        else -> offsetX.animateTo(0f, spring())
                                    }
                                }
                            },
                            onDragCancel = {
                                scope.launch { offsetX.animateTo(0f, spring()) }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = (offsetX.value + dragAmount.x)
                                    .coerceIn(-maxReveal, maxReveal)
                                scope.launch { offsetX.snapTo(newOffset) }
                            }
                        )
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onItemSelected
                )
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product?.name ?: "Không tìm thấy sản phẩm",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Hiển thị thêm thông tin phân loại để dễ nhìn
                    if (variant != null) {
                        Text(
                            text = "Loại: ${variant.color.replaceFirstChar { it.uppercase() }}, Size ${variant.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${variant?.price?.formatMoney() ?: 0}",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
