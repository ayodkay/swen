package com.ayodkay.apps.swen.helper.room.bookmarks

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newsroom")
data class BookMarkRoom(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "source") val source: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "urlToImage") val urlToImage: String,
    @ColumnInfo(name = "publishedAt") val publishedAt: String,
    @ColumnInfo(name = "content") val content: String
)
