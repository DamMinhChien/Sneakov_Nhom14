package com.firebase.sneakov.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.compose.ProductCard
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.WishlistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WishlistScreen(wishlistViewModel: WishlistViewModel = koinViewModel(), onProductClick: (Product) -> Unit) {
    val context = LocalContext.current
    val wishlistUiState by wishlistViewModel.uiState.collectAsState()

//    LaunchedEffect(true) {
//        wishlistViewModel.fetchWishlistWithProducts()
//    }

    RefreshableLayout(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        isRefreshing = wishlistUiState.isLoading,
        onRefresh = {
            wishlistViewModel.fetchWishlistWithProducts()
        }) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp)) {
            when {
                wishlistUiState.error != null -> {
                    Toast.makeText(context, wishlistUiState.error, Toast.LENGTH_LONG).show()
                    Log.d("bac", wishlistUiState.error!!)
                }

                wishlistUiState.data != null -> {
                    @Suppress("UNCHECKED_CAST")
                    val products = (wishlistUiState.data as? List<Product>).orEmpty()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(products) { product ->
                            ProductCard(
                                product = product,
                                onClick = { onProductClick(product) },
                                onFavoriteClick = { product ->
                                    // Add to wishlist
                                    wishlistViewModel.removeFromWishlist(product.id)
                                },
                                isFavorite = true
                            )
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Không có dữ liệu sản phẩm yêu thích",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}