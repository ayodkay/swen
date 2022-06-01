package com.ayodkay.apps.swen.helper.room.bookmarks

import androidx.lifecycle.LiveData

class BookMarkRoomRepo(private val roomDao: BookMarkDao) {
    val allBookMarkRoom: LiveData<List<BookMarkRoom>> = roomDao.getAll()

    suspend fun insert(bookMark: BookMarkRoom) {
        roomDao.insertAll(bookMark)
    }

    fun deleteOne(url: String) {
        roomDao.deleteOne(url)
    }

    fun exist(url: String): Boolean {
        return roomDao.exist(url)
    }

    suspend fun delete() {
        roomDao.delete()
    }
}
