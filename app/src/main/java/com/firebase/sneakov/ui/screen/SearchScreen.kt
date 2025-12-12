package com.firebase.sneakov.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.compose.FilterBottomSheet
import com.firebase.sneakov.ui.compose.ProductCard
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.ui.compose.SearchBar
import com.firebase.sneakov.viewmodel.BrandsNameViewModel
import com.firebase.sneakov.viewmodel.ColorViewModel
import com.firebase.sneakov.viewmodel.ProductFilter
import com.firebase.sneakov.viewmodel.SearchViewModel
import com.firebase.sneakov.viewmodel.SortField
import com.firebase.sneakov.viewmodel.SortOrder
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    keyword: String,
    brand: String?,
    latest: Boolean,
    onProductClick: (Product) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
    colorViewModel: ColorViewModel = koinViewModel(),
    brandsNameViewModel: BrandsNameViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filter.collectAsState()
    val colorsState by colorViewModel.uiState.collectAsState()
    val brandsNameState by brandsNameViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showFilterSheet by remember { mutableStateOf(false) }
    var sortByLatest by remember { mutableStateOf(latest) }
    var sortByPriceAsc by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf(keyword) }

//    LaunchedEffect(Unit) {
//        viewModel.fetchProducts()
//        colorViewModel.getColorsName()
//        brandsNameViewModel.getBrandsName()
//    }

    LaunchedEffect(keyword, brand, latest) {
        viewModel.updateFilter {
            copy(
                keyword = keyword,
                brand = brand,
                sortBy = if (latest) SortField.CREATED_AT else SortField.NAME,
                sortOrder = SortOrder.DESC
            )
        }
    }

    RefreshableLayout(
        isRefreshing = uiState.isLoading,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        onRefresh = { viewModel.fetchProducts() }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 🔍 Search + Filter icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { viewModel.updateFilter { copy(keyword = query) } },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Filter,
                        contentDescription = "Filter",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔸 Hàng chọn nhanh: Mới nhất / Giá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    sortByLatest = !sortByLatest
                    viewModel.updateFilter {
                        copy(
                            sortBy = if (sortByLatest) SortField.CREATED_AT else SortField.NAME,
                            sortOrder = SortOrder.DESC
                        )
                    }
                }) {
                    Text(if (sortByLatest) "🔥 Mới nhất" else "Mặc định", style = MaterialTheme.typography.bodyLarge)
                }

                TextButton(onClick = {
                    sortByPriceAsc = !sortByPriceAsc
                    viewModel.updateFilter {
                        copy(
                            sortBy = SortField.PRICE,
                            sortOrder = if (sortByPriceAsc) SortOrder.ASC else SortOrder.DESC
                        )
                    }
                }) {
                    Text("Giá ${if (sortByPriceAsc) "↑" else "↓"}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🧩 Danh sách sản phẩm
            if (uiState.data != null) {
                val products = uiState.data!!
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(products) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            } else if (uiState.error != null) {
                Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            } else if(colorsState.error != null){
                Toast.makeText(context, colorsState.error, Toast.LENGTH_LONG).show()
            }else if(brandsNameState.error != null){
                Toast.makeText(context, brandsNameState.error, Toast.LENGTH_LONG).show()
            }
            else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có dữ liệu sản phẩm")
                }
            }
        }
    }

    // 🧭 BottomSheet lọc chi tiết
    FilterBottomSheet(
        show = showFilterSheet,
        onDismiss = { showFilterSheet = false },
        currentFilter = filterState,
        onApply = { newFilter ->
            viewModel.updateFilter { newFilter }
            viewModel.fetchProducts()
            showFilterSheet = false
        },
        onReset = {
            viewModel.updateFilter { ProductFilter() }
            viewModel.fetchProducts()
            showFilterSheet = false
        },
        brands = brandsNameState.data ?: emptyList(),
        colors = colorsState.data ?: emptyList(),
        sizes = listOf(36,37,38,39,40,41,42,43,44)
    )
}


