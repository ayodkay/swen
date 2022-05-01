package com.ayodkay.apps.swen.view.search

import androidx.appcompat.widget.SearchView
import androidx.databinding.BindingAdapter


@BindingAdapter("app:doOnQueryTextListener")
inline fun SearchView.doOnQueryTextListener(listener: SearchViewListener): SearchView.OnQueryTextListener {
    val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            listener.onQueryTextSubmit(query ?: "")
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean = true
    }
    this.setOnQueryTextListener(queryListener)
    return queryListener
}

interface SearchViewListener {
    fun onQueryTextSubmit(s: String)
}