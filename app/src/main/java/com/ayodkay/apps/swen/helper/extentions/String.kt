package com.ayodkay.apps.swen.helper.extentions

inline fun <T> T?.ifNull(defaultValue: () -> T): T {
    return this ?: defaultValue()
}

fun <T> T?.isNull(): Boolean = this == null

fun <T> T?.isNotNull(): Boolean = this != null
