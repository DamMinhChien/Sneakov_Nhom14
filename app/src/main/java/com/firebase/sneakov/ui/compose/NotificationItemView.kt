package com.firebase.sneakov.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationItemView(item: Notification, onMarkRead: (String) -> Unit, onDelete: (String) -> Unit) {
    var showDeleteButton by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(4.dp)
            .padding(bottom = 4.dp)
    ) {
      Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { showDeleteButton = true}
            )
    ) {
        DropdownMenu(
            expanded = showDeleteButton,
            onDismissRequest = { showDeleteButton = false }
        ) {
            DropdownMenuItem(
                text = { Text("Xoá") },
                onClick = { onDelete(item.id)}
            )
        }
        // Dòng 1: Title
        Text(
            text = item.title,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(Modifier.height(4.dp))

        // Dòng 2: Body
        Text(
            text = item.body,
            color = Color(0xFF444444)
        )

        Spacer(Modifier.height(4.dp))

        // Dòng 3: Thời gian (small + nhạt)
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
          ) {

              // Time
              Text(
                  text = formatFullTime(item.createdAt.toDate()),
                  fontSize = MaterialTheme.typography.bodySmall.fontSize,
                  color = Color.Gray
              )

              // Button only if not yet read
              if (!item.read) {
                  Text(
                      text = "Đã đọc",
                      color = Color(0xFF1A73E8),
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier
                          .padding(start = 12.dp)
                          .background(Color(0xFFE8F0FE), RoundedCornerShape(8.dp))
                          .padding(horizontal = 10.dp, vertical = 6.dp)
                          .clickable {
                              onMarkRead(item.id)
                          }
                  )
              }
          }
        }
    }
}

fun formatFullTime(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    return formatter.format(date)
}
//fun timeAgo(date: Date) :String {
//    val diff = System.currentTimeMillis() - date.time
//    val minutes = diff / 60000
//    return when{
//        minutes < 1 -> "Vừa xong"
//        minutes < 60 -> "$minutes phút trước"
//        else -> "${minutes / 60} giờ trước"
//    }
//}