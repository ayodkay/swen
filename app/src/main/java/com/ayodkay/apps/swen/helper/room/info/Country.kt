package com.ayodkay.apps.swen.helper.room.info

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Country(
    @PrimaryKey val country: String,
    val iso: String
)