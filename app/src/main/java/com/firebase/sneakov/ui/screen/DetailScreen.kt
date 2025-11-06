package com.firebase.sneakov.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import com.firebase.sneakov.ui.compose.ProductDetailContent
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.DetailViewModel
import com.firebase.sneakov.viewmodel.HelperViewModel
import com.firebase.sneakov.viewmodel.WishlistViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
    helperViewModel: HelperViewModel = koinViewModel(),
    wishlistViewModel: WishlistViewModel = koinViewModel(),
    id: String
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val helperState by helperViewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.fetchProduct(id)
    }

    LaunchedEffect(Unit) {
        helperViewModel.fetchWishlistIds()
    }

    RefreshableLayout(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = {
            viewModel.fetchProduct(id)
        }) {
        Box(modifier = Modifier.fillMaxSize()){
            when {
                uiState.error != null -> {
                    Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
                }

                uiState.data != null -> {
                    val wishlistIds = helperState.data.orEmpty()
                    var isFavorite by remember { mutableStateOf(wishlistIds.contains(uiState.data!!.id)) }
                    ProductDetailContent(product = uiState.data!!, isFavorite = isFavorite, onFavoriteClick = {
                        isFavorite = !isFavorite
                        if (isFavorite) wishlistViewModel.addToWishlist(uiState.data!!.id) else wishlistViewModel.removeFromWishlist(uiState.data!!.id)
                    })
                }

                else -> {
                    Text(
                        text = "Không có dữ liệu sản phẩm",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
