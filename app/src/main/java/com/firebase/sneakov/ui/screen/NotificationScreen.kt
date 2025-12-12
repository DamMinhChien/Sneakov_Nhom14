package com.firebase.sneakov.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.data.model.Notification
import com.firebase.sneakov.ui.compose.NotificationItemView
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.NotificationViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(viewModel: NotificationViewModel) {
    val list by viewModel.state.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

//    LaunchedEffect(Unit) {
//        viewModel.loadNotifications()
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RefreshableLayout(
            isRefreshing = isLoading,
            onRefresh = { viewModel.loadNotifications()},
            modifier = Modifier.fillMaxSize()
        ){
            if(!isLoading) {
                val (recent, yesterday) = splitNotifications(list)
                if (recent.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Không có thông báo nào",
                            textAlign = TextAlign.Center
                        )
                    }
                }else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if(recent.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Recent",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1C1C1C),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(recent) {
                                NotificationItemView(it, onMarkRead = {id -> viewModel.markRead(id)}, onDelete = { viewModel.delNotification(it)})
                            }
                        }
                        if (yesterday.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Yesterday",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF1C1C1C),
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            items(yesterday) { NotificationItemView(it, onMarkRead = {id -> viewModel.markRead(id)}, onDelete = { viewModel.delNotification(it)}) }
                        }
                    }
                }
            }

        }


    }
}
fun splitNotifications(list: List<Notification>): Pair<List<Notification>, List<Notification>> {
    // Nếu danh sách rỗng, trả về ngay để tránh xử lý không cần thiết
    if (list.isEmpty()) {
        return Pair(emptyList(), emptyList())
    }

    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    val todayString = sdf.format(Date())

    // Sử dụng groupBy để phân loại an toàn và hiệu quả hơn
    val groupedNotifications = list
        .filter { it.createdAt != null } // BƯỚC 1: Lọc bỏ tất cả các notification có createdAt là null
        .groupBy { notification ->
            // BƯỚC 2: Phân nhóm theo ngày
            val notificationDateString = sdf.format(notification.createdAt!!.toDate()) // Dấu !! an toàn vì đã filter ở trên
            if (notificationDateString == todayString) "recent" else "yesterday"
        }

    // Lấy ra danh sách từ các nhóm đã phân loại
    val recent = groupedNotifications["recent"] ?: emptyList()
    val yesterday = groupedNotifications["yesterday"] ?: emptyList()

    return Pair(recent, yesterday)
}
