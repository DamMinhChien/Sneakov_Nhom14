package com.firebase.sneakov.utils

import android.content.Context
import androidx.core.content.edit

fun setOnboardingSeen(context: Context, seen: Boolean) {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    prefs.edit { putBoolean(Prefs.IS_SEEN_ONBOARDING, seen) }
}

fun isOnboardingSeen(context: Context): Boolean {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    return prefs.getBoolean(Prefs.IS_SEEN_ONBOARDING, false)
}

fun savePrefsBoolean(context: Context, key: String, value: Boolean) {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    prefs.edit { putBoolean(key, value) }
}

fun savePrefsString(context: Context, key: String, value: String){
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    prefs.edit { putString(key, value) }
}

fun savePrefsInteger(context: Context, key: String, value: Int) {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    prefs.edit { putInt(key, value) }
}

fun loadPrefsBoolean(context: Context, key: String): Boolean {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    return prefs.getBoolean(key, false)
}

fun loadPrefsString(context: Context, key: String): String {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    return prefs.getString(key, "")!!
}

fun loadPrefsInteger(context: Context, key: String): Int {
    val prefs = context.getSharedPreferences(Prefs.APP, Context.MODE_PRIVATE)
    return prefs.getInt(key, 0)
}