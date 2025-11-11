package com.firebase.sneakov.utils

import android.content.Context

fun setOnboardingSeen(context: Context, seen: Boolean) {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(Prefs.IS_SEEN_ONBOARDING, seen).apply()
}

fun isOnboardingSeen(context: Context): Boolean {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    return prefs.getBoolean(Prefs.IS_SEEN_ONBOARDING, false)
}
