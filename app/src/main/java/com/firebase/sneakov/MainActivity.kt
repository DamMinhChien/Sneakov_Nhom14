package com.firebase.sneakov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.firebase.sneakov.ui.app.SneakovApp
import com.firebase.sneakov.ui.theme.SneakovTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            val context = LocalContext.current
//            Button(
//                onClick = {
//                    FirebaseUploader.uploadProducts(context)
//                }
//            ) {
//                Text("Click")
//            }
            SneakovApp()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    SneakovTheme {
        SneakovApp()
    }
}