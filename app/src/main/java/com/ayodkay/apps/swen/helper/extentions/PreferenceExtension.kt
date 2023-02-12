package com.ayodkay.apps.swen.helper.extentions

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class PreferenceExtension<T>(val context: Context, val key: String, val defaultValue: T) {
    private val preference: SharedPreferences by lazy {
        context.getSharedPreferences(
            "swen", Context.MODE_PRIVATE
        )
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreferences(key, defaultValue)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        savePreferences(key, value)
    }

    private fun savePreferences(key: String, value: T) {
        with(preference.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException()
            }.apply()
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun findPreferences(key: String, defaultValue: T): T {
        with(preference) {
            val result: Any? = when (defaultValue) {
                is Boolean -> getBoolean(key, defaultValue)
                is Int -> getInt(key, defaultValue)
                is Long -> getLong(key, defaultValue)
                is Float -> getFloat(key, defaultValue)
                is String -> getString(key, defaultValue)
                else -> throw IllegalArgumentException()
            }
            return result as T
        }
    }
}
