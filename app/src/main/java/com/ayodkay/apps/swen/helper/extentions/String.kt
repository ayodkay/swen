package com.ayodkay.apps.swen.helper.extentions

fun String.ifNull(defaultValue: () -> String): String {
    return if (isNullOrEmpty()) defaultValue() else this
}