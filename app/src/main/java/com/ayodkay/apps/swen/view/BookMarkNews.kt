package com.ayodkay.apps.swen.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.RoomRecyclerview
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.ayodkay.apps.swen.model.News
import kotlinx.android.synthetic.main.activity_bookmark_news.*
import java.util.*

class BookMarkNews : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
            } // Night mode is active, we're using dark theme
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_news)



    }
}