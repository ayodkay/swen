package com.ayodkay.apps.swen.view.home.category

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


@BindingAdapter("refreshing")
fun SwipeRefreshLayout.refreshing(refreshing: Boolean) {
    if (isRefreshing) {
        this.isRefreshing = refreshing
    }
}