package com.ayodkay.apps.swen.helper.room.userlocation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val latitude: Double? = 0.toDouble(),
    val longitude: Double? = 0.toDouble(),
    var countryCode: String? = "swen",
    @PrimaryKey var country: String
)
