package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.ui.theme.SneakovTheme
import com.firebase.sneakov.R
import kotlinx.coroutines.launch
import kotlin.collections.lastIndex

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(navToLogin: () -> Unit) {

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.intro1,
            title = "Chào mừng đến với ${stringResource(R.string.app_name)}",
            description = "Khám phá thế giới sneaker chính hãng, nơi phong cách và chất lượng hòa quyện !"
        ),
        OnboardingPage(
            imageRes = R.drawable.intro2,
            title = "Bước đi êm ái, tự tin mỗi ngày",
            description = "Mỗi đôi Sneaker được thiết kế để mang lại cảm giác thoải mái và tự tin trên từng bước chân !"
        ),
        OnboardingPage(
            imageRes = R.drawable.intro3,
            title = "Đặt hàng dễ dàng – Giao hàng nhanh chóng",
            description = "${stringResource(R.string.app_name)} sẽ giao tận tay chỉ trong tích tắc !"
        )
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            modifier = Modifier
                .align(Alignment.End)
                .alpha(if (pagerState.currentPage != pages.lastIndex) 1f else 0f),
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(pages.lastIndex)
                }
            }
        ) {
            Text(
                "Bỏ qua",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            val data = pages[page]
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(data.imageRes),
                    contentDescription = null,
                    modifier = Modifier.padding(top = 40.dp)
                        .clip(CircleShape)
                        .size(300.dp),
                    contentScale = ContentScale.Crop

                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = data.description,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ){
            Row {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Button
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navToLogin()
                    }
                },
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.lastIndex) "Bắt đầu" else "Tiếp theo",
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingPreview() {
    SneakovTheme {
        OnboardingScreen(navToLogin = {})
    }
}