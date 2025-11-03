package com.firebase.sneakov.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.firebase.sneakov.ui.compose.ProductDetailContent
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = koinViewModel(),
    id: String
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

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
                    Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
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
