package com.ayodkay.apps.swen.helper.extentions

fun Int?.orEmpty(default: Int = 0): Int {
    return this ?: default
}
