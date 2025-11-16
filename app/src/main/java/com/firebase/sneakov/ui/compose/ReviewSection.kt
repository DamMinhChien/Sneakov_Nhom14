package com.firebase.sneakov.ui.compose

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Review
import com.firebase.sneakov.viewmodel.ReviewViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSection(
    productId: String,
    reviewViewModel: ReviewViewModel = koinViewModel(),
    onRatingCalculated: (Float) -> Unit,
    userViewModel: UserViewModel = koinViewModel()
) {
    val uiState by reviewViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Fetch khi vào màn hình
    LaunchedEffect(productId) {
        reviewViewModel.fetchReviewsByProductId(productId)
    }

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    LaunchedEffect(uiState.data) {
        val reviews = uiState.data
        if (reviews?.isNotEmpty() ?: false) {
            val avg = reviews.map { it.rating }.average().toFloat()
            onRatingCalculated(avg)  // gửi cho cha
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // ---- Phần thêm review ----
        AddReviewSection(
            rating = rating,
            onRatingChange = { rating = it },
            comment = comment,
            onCommentChange = { comment = it },
            onSubmit = {
                if (rating == 0 || comment.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                    return@AddReviewSection
                }

                val newReview = Review(
                    productId = productId,
                    rating = rating,
                    comment = comment,
                    username = userState.data?.name ?: "Người dùng"
                )
                reviewViewModel.createReview(newReview)
                rating = 0
                comment = ""
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---- Hiển thị danh sách ----
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(
                    text = uiState.error ?: "Lỗi không xác định",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            uiState.data.isNullOrEmpty() -> {
                Text("Chưa có đánh giá nào.", modifier = Modifier.padding(8.dp))
            }

            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.data!!.forEach { review ->
                        ReviewItem(review)
                    }
                }
            }
        }
    }
}
