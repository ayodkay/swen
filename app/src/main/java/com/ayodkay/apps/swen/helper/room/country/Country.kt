package com.ayodkay.apps.swen.helper.room.country

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Country(
    @PrimaryKey val country: String,
    var iso: String,
    var position: Int? = 2
)