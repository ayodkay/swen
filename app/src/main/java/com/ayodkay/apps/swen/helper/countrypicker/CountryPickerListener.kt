package com.ayodkay.apps.swen.helper.countrypicker

interface CountryPickerListener {
    fun onSelectCountry(name: String?, code: String?,iso: String?,language: String?)
}