package com.ayodkay.apps.swen.helper.extentions

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.ayodkay.apps.swen.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

@BindingAdapter("app:imageUrl")
fun ImageView.imageUrl(url: String) {
    val imageView = this
    try {
        Picasso.get().load(url).into(imageView, object : Callback {
            override fun onSuccess() {}
            override fun onError(e: Exception?) {
                setEmptyImage(imageView)
            }
        })
    } catch (e: Exception) {
        setEmptyImage(imageView)
    }
}

private fun setEmptyImage(imageView: ImageView) {
    imageView.apply {
        setImageDrawable(ResourcesCompat.getDrawable(resources,
            R.drawable.ic_undraw_page_not_found_su7k, null))
    }
}