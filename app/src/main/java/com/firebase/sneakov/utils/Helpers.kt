package com.firebase.sneakov.utils

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

fun String.capitalizeFirst(): String =
    this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun Double.formatMoney(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    return formatter.format(this)
}

fun Long.formatMoney(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    return formatter.format(this)
}

fun Int.formatMoney(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"))
    return formatter.format(this)
}

fun Color.Companion.fromHex(hex: String): Color {
    // Xóa # nếu có
    val cleanHex = hex.removePrefix("#")
    // Thêm alpha FF nếu hex chỉ có 6 ký tự
    val argbHex = when (cleanHex.length) {
        6 -> "FF$cleanHex"       // #RRGGBB -> #FFRRGGBB
        8 -> cleanHex            // #AARRGGBB
        else -> throw IllegalArgumentException("Invalid hex color: $hex")
    }
    return Color(argbHex.toLong(16))
}

fun formatDateString(raw: String): String {
    return try {
        val inputFormat = SimpleDateFormat("EEE MMM dd 'GMT'Z yyyy", Locale.ENGLISH)
        val date = inputFormat.parse(raw)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        outputFormat.format(date!!)
    } catch (e: Exception) {
        raw // nếu lỗi thì trả lại chuỗi gốc
    }
}


