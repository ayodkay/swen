package com.ayodkay.apps.swen.helper.room.bookmarks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookmarkRoomVM(application: Application) : AndroidViewModel(application) {
    private val repository: BookMarkRoomRepo

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allBookMarkRoom: LiveData<List<BookMarkRoom>>

    init {
        val newsDao = BookMarkDatabase.getDatabase(application, viewModelScope).newsDao()
        repository = BookMarkRoomRepo(newsDao)
        allBookMarkRoom = repository.allBookMarkRoom
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(bookMark: BookMarkRoom) = viewModelScope.launch {
        repository.insert(bookMark)
    }

    fun deleteOne(url: String) {
        repository.deleteOne(url)
    }

    fun exist(url: String): Boolean {
        return repository.exist(url)
    }

    fun nuke() = viewModelScope.launch {
        repository.delete()
    }

}