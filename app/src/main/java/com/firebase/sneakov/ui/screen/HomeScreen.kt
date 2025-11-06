package com.firebase.sneakov.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.firebase.sneakov.R
import com.firebase.sneakov.data.model.Brand
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.ui.compose.BaseCard
import com.firebase.sneakov.ui.compose.ProductCard
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.ui.compose.SearchBar
import com.firebase.sneakov.viewmodel.BrandViewModel
import com.firebase.sneakov.viewmodel.CartViewModel
import com.firebase.sneakov.viewmodel.HelperViewModel
import com.firebase.sneakov.viewmodel.ProductViewModel
import com.firebase.sneakov.viewmodel.WishlistViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    brandViewModel: BrandViewModel = koinViewModel(),
    goToSearchScreen: (query: String) -> Unit,
    goToSearchScreenWithBrand: (brand: Brand) -> Unit,
    goToSearchScreenWithLatest: () -> Unit,
    productViewModel: ProductViewModel = koinViewModel(),
    cartViewModel: CartViewModel = koinViewModel(),
    onProductClick: (Product) -> Unit,
    helperViewModel: HelperViewModel = koinViewModel(),
    wishlistViewModel: WishlistViewModel = koinViewModel()
) {
    val brandState by brandViewModel.uiState.collectAsState()
    val productState by productViewModel.uiState.collectAsState()
    val helperState by helperViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth / 2

    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Log.d("Check", "Category LaunchedEffect chạy")
        brandViewModel.fetchBrands()
    }
    LaunchedEffect(Unit) {
        Log.d("Check", "Search LaunchedEffect chạy")
        productViewModel.fetch10NewestProducts()
    }
    LaunchedEffect(Unit) {
        helperViewModel.fetchWishlistIds()
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    goToSearchScreen(query)
                },
                modifier = Modifier.padding(0.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            //            Text("abcwwkfhs: ${uiState.result}")
            Text("Danh mục sản phẩm", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(8.dp))

//             Danh sách danh mục
            if (!brandState.data.isNullOrEmpty()) {
                val brands = brandState.data!!
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = brands) { brand ->
                        BaseCard(
                            onClick = {
                                goToSearchScreenWithBrand(brand)
                            }
                        ) {
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

                TextButton(
                    onClick = {
                        goToSearchScreenWithLatest()
                    }
                ){
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Log.d("products", "products: ${productState.data}")
            if (!productState.data.isNullOrEmpty()) {
                val products = productState.data!!
                val wishlistIds = helperState.data.orEmpty()
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items = products) { product ->
                        var isFavorite by remember { mutableStateOf(wishlistIds.contains(product.id)) }
                        ProductCard(
                            modifier = Modifier
                                .width(itemWidth),
                            product = product,
                            onClick = onProductClick,
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                // Add to wishlist
                                isFavorite = !isFavorite
                                if (isFavorite) wishlistViewModel.addToWishlist(product.id) else wishlistViewModel.removeFromWishlist(product.id)
                            },
                            onAddToCart = { selectedProd ->
                                val defaultVarian = selectedProd?.variants?.firstOrNull()
                                Log.d("HomeScreen", "Variant stock = ${defaultVarian?.stock}")
                                if(defaultVarian != null) {
                                    cartViewModel.addToCart(
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

//@Composable
//@Preview(showSystemUi = true, showBackground = true)
//fun HomePreViewNice() {
//    SneakovTheme {
//        HomeScreen(
//            brandViewModel = TODO(),
//            goToSearchScreen = TODO(),
//            productViewModel = TODO(),
//            onProductClick = TODO(),
//            helperViewModel = TODO(),
//            wishlistViewModel = TODO()
//        )
//    }
//}