package com.firebase.sneakov.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AuthCard(modifier: Modifier, onNavigateToHome: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        AnimatedContent(targetState = isLogin) {showLogin ->
            if(showLogin){
                LoginForm(onNavigateToHome = onNavigateToHome) {
                    isLogin = false
                }
            }
            else{
                RegisterForm(onSuccessRegister = { isLogin = true }) {
                    isLogin = true
                }
            }
        }
    }
}