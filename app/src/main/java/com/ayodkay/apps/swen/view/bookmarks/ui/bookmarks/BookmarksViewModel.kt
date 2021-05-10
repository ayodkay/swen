package com.ayodkay.apps.swen.view.bookmarks.ui.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.model.News
import kotlinx.coroutines.withContext
import java.util.ArrayList

class BookmarksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

}