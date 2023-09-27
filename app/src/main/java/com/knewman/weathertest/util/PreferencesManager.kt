package com.knewman.weathertest.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.IllegalArgumentException

class PreferencesManager(context: Context) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    operator fun set(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    @Throws(IllegalArgumentException::class)
    inline operator fun <reified T> set(key: String, value: T) {
        sharedPreferences.edit {
            putString(key, Json.encodeToString(value))
        }
    }

    operator fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }
}