package com.ayodkay.apps.swen.helper.extentions

inline fun String?.ifNull(defaultValue: () -> String): String {
    return if (isNullOrEmpty()) defaultValue() else this
}