package com.ayodkay.apps.swen.view.search

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.ayodkay.apps.swen.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    var queryValue: String = "null"
    lateinit var sort : String
    private var sortOptions = arrayListOf("popularity","publishedAt","relevancy")




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
        setContentView(R.layout.activity_search)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.item_search_container, SearchFragment())
                .commit()
        }

        val singleSort = arrayOf(
            getString(R.string.popularity),
            getString(R.string.newest),
            getString(R.string.relevancy)
        )
        var checkedSort = 1
        sort = sortOptions[checkedSort]



        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queryValue = query.toString()
                loadNews(queryValue)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        sortBy.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.sort_news))
                .setNeutralButton(resources.getString(android.R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(android.R.string.ok)) { _, _ ->
                    if (queryValue != "null"){
                        loadNews(queryValue)
                    }

                }
                // Single-choice items (initialized with checked item)
                .setSingleChoiceItems(singleSort, checkedSort) { _, which ->
                    sort = sortOptions[which]
                    checkedSort = which
                }
                .show()
        }
    }




}