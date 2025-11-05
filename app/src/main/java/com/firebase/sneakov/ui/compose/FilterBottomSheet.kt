package com.firebase.sneakov.ui.compose

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.viewmodel.ProductFilter
import com.firebase.sneakov.viewmodel.SortField
import com.firebase.sneakov.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    currentFilter: ProductFilter,
    onApply: (ProductFilter) -> Unit,
    onReset: () -> Unit,
    brands: List<String>,
    colors: List<String>,
    sizes: List<Int>,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedBrand by remember { mutableStateOf(currentFilter.brand) }
    var selectedColor by remember { mutableStateOf(currentFilter.color) }
    var selectedSize by remember { mutableStateOf(currentFilter.size) }
    var sortBy by remember { mutableStateOf(currentFilter.sortBy) }
    var sortOrder by remember { mutableStateOf(currentFilter.sortOrder) }

    if (show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Bộ lọc nâng cao",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Thương hiệu ---
                Text("Thương hiệu:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    brands.forEach { brand ->
                        FilterChip(
                            label = { Text(brand) },
                            selected = selectedBrand == brand,
                            onClick = {
                                selectedBrand = if (selectedBrand == brand) null else brand
                            }
                        )
                    }
                }

                // --- Màu sắc ---
                Text("Màu sắc:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    colors.forEach { color ->
                        FilterChip(
                            label = { Text(color) },
                            selected = selectedColor == color,
                            onClick = {
                                selectedColor = if (selectedColor == color) null else color
                            }
                        )
                    }
                }

                // --- Kích thước ---
                Text("Kích thước:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    sizes.forEach { size ->
                        FilterChip(
                            label = { Text(size.toString()) },
                            selected = selectedSize == size,
                            onClick = {
                                selectedSize = if (selectedSize == size) null else size
                            }
                        )
                    }
                }

                // --- Sắp xếp ---
                Text("Sắp xếp theo:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    SortField.entries.forEach {
                        FilterChip(
                            label = { Text(it.name) },
                            selected = sortBy == it,
                            onClick = { sortBy = it }
                        )
                    }
                }

                // --- Thứ tự ---
                Text("Thứ tự:")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    SortOrder.entries.forEach {
                        FilterChip(
                            label = { Text(it.name) },
                            selected = sortOrder == it,
                            onClick = { sortOrder = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Thiết lập lại")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onApply(
                                currentFilter.copy(
                                    brand = selectedBrand,
                                    color = selectedColor,
                                    size = selectedSize,
                                    sortBy = sortBy,
                                    sortOrder = sortOrder
                                )
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Áp dụng")
                    }

                }


                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
