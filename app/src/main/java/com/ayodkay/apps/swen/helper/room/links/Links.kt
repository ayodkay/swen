package com.ayodkay.apps.swen.helper.room.links

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class Links(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var link: String,
)
