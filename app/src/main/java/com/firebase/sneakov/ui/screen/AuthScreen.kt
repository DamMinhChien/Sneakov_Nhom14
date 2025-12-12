package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.R
import com.firebase.sneakov.ui.compose.AuthCard
import com.firebase.sneakov.ui.theme.SneakovTheme

@Composable
fun AuthScreen(onNavigateToHome: () -> Unit, goToResetPasswordScreen: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF3C3B3F), Color(0xFF605C3C))
                )
            ),

        ) {
        Box(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(200.dp)
                    .background(MaterialTheme.colorScheme.surface),
                contentScale = ContentScale.Crop
            )
        }
        AuthCard(
            modifier = Modifier.align(Alignment.BottomCenter),
            onNavigateToHome = onNavigateToHome,
            goToResetPasswordScreen = goToResetPasswordScreen
        )
    }

}