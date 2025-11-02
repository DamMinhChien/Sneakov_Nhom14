package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.DetailViewModel
import com.firebase.sneakov.ui.compose.ProductDetailContent
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
    id: String
) {
    val uiState by viewModel.uiState.collectAsState()

    // Lấy dữ liệu khi mở màn hình
    LaunchedEffect(id) {
        viewModel.fetchProduct(id)
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
                    Text(
                        text = "Lỗi: ${uiState.error}",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.data != null -> {
                    ProductDetailContent(product = uiState.data!!)
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
