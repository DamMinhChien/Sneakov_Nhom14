package com.firebase.sneakov.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.firebase.sneakov.R
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.compose.BaseCard
import com.firebase.sneakov.ui.compose.ProductCard
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.ui.theme.SneakovTheme
import com.firebase.sneakov.viewmodel.BrandViewModel
import com.firebase.sneakov.viewmodel.CartViewModel
import com.firebase.sneakov.viewmodel.ProductViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    brandViewModel: BrandViewModel = koinViewModel(),
    navigateToSearchScreen: () -> Unit,
    productViewModel: ProductViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    onProductClick: (Product) -> Unit
) {
    val brandState by brandViewModel.uiState.collectAsState()
    val productState by productViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth / 2

    LaunchedEffect(Unit) {
        Log.d("Check", "Category LaunchedEffect chạy")
        brandViewModel.fetchBrands()
    }
    LaunchedEffect(Unit) {
        Log.d("Check", "Search LaunchedEffect chạy")
        productViewModel.fetch10NewestProducts()
    }
    RefreshableLayout(
        isRefreshing = brandState.isLoading || productState.isLoading,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        onRefresh = {
            brandViewModel.fetchBrands()
            productViewModel.fetch10NewestProducts()
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .clickable { navigateToSearchScreen() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tìm kiếm...",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            //            Text("abcwwkfhs: ${uiState.result}")
            Text("Danh mục sản phẩm", style = MaterialTheme.typography.bodyLarge)

//             Danh sách danh mục
            if (!brandState.data.isNullOrEmpty()) {
                val brands = brandState.data!!
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = brands) { brand ->
                        BaseCard {
                            AsyncImage(
                                model = brand.thumbnail,
                                contentDescription = brand.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(8.dp),
                                contentScale = ContentScale.Crop,
                                error = painterResource(R.drawable.img_place_error),
                                placeholder = painterResource(R.drawable.img_place_holder)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Sản phẩm mới
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Hàng mới về", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Xem tất cả",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Log.d("products", "products: ${productState.data}")
            if (!productState.data.isNullOrEmpty()) {
                val products = productState.data!!
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = products) { product ->
                        Log.d("Check", "product: $product")
                        Log.d("HomeScreen", "Variant stock = ${product?.variants?.firstOrNull()?.stock}")
                        ProductCard(
                            modifier = Modifier
                                .width(itemWidth),
                            product = product,
                            onClick = onProductClick,
                            onAddToCart = { selectedProd ->
                                val defaultVarian = selectedProd?.variants?.firstOrNull()
                                Log.d("HomeScreen", "Variant stock = ${defaultVarian?.stock}")
                                if(defaultVarian != null) {
                                    cartViewModel.addToCart(
                                        userId = "user_001",
                                        productId = selectedProd.id,
                                        variantId = defaultVarian.id,
                                        quantity = 1
                                    )
                                }
                            }
                        )
                    }

                }
            } else {
                Text(text = "Không có sản phẩm nào!", textAlign = TextAlign.Center)
            }
        }
    }
    if (brandState.error != null) {
        Toast.makeText(context, brandState.error, Toast.LENGTH_LONG).show()
        brandViewModel.dismissError()
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun HomePreViewNice() {
    SneakovTheme {
        HomeScreen(
            brandViewModel = TODO(),
            navigateToSearchScreen = TODO(),
            productViewModel = TODO(),
            onProductClick = TODO()
        )
    }
}