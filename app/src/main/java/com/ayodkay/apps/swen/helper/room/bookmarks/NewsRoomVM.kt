package com.ayodkay.apps.swen.helper.room.bookmarks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NewsRoomVM(application: Application) : AndroidViewModel(application) {
    private val repository: NewsRoomRepo

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allNewsRoom: LiveData<List<NewsRoom>>

    init {
        val newsDao = NewsDatabase.getDatabase(application, viewModelScope).newsDao()
        repository = NewsRoomRepo(newsDao)
        allNewsRoom = repository.allNewsRoom
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(news: NewsRoom) = viewModelScope.launch {
        repository.insert(news)
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