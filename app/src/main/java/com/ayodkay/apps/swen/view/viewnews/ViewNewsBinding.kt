package com.ayodkay.apps.swen.view.viewnews

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayodkay.apps.swen.helper.adapter.MaxAdsRecyclerView
import com.github.ayodkay.models.Article


@BindingAdapter("currentOrders")
fun RecyclerView.setCurrentOrders(
    newsList: ArrayList<Article>?,
    orderListener: OrderListener,
    curbsideOrders: ArrayList<CurbsideOrderInfo>?,
) {
    if (newsList != null) {
        if (adapter == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MaxAdsRecyclerView(newsList)
        } else {
            (adapter as CurrentOrdersAdapter).apply {
                this.recentOrders = recentOrders
                notifyDataSetChanged()
            }
        }
    }
}