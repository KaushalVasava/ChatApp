package com.kmv.chatapp.utils

import android.content.Context

class SharedPref(val context: Context) {

    val shared = context.getSharedPreferences("CHAT", Context.MODE_PRIVATE)
    var userName: String
        get() {
            return shared.getString("userName", "") ?: ""
        }
        set(value) {
            shared.edit().putString("userName", value).apply()
        }
}