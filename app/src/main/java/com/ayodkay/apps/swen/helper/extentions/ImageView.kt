package com.ayodkay.apps.swen.helper.extentions

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.view.zoom.ZoomClass
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

@BindingAdapter("app:imageUrl", "app:loadingCallBack")
fun AppCompatImageView.loadUrl(url: String, callBack: ImageViewCallBack?) {
    val imageView = this
    try {
        Picasso.get().load(url).into(
            imageView,
            object : Callback {
                override fun onSuccess() {
                    callBack?.onLoadingDone()
                }

                override fun onError(e: Exception?) {
                    setEmptyImage(imageView)
                    callBack?.onLoadingDone()
                }
            }
        )
    } catch (e: Exception) {
        setEmptyImage(imageView)
        callBack?.onLoadingDone()
    }
}

@BindingAdapter("app:zoomUrl")
fun ZoomClass.loadUrl(url: String) {
    val imageView = this
    try {
        Picasso.get().load(url).into(
            imageView,
            object : Callback {
                override fun onSuccess() {}

                override fun onError(e: Exception?) {
                    setEmptyImage(imageView)
                }
            }
        )
    } catch (e: Exception) {
        setEmptyImage(imageView)
    }
}

@BindingAdapter("app:imageDrawableId")
fun ImageView.setImageDrawable(id: Int) {
    setImageDrawable(
        ResourcesCompat.getDrawable(resources, id, null)
    )
}

private fun setEmptyImage(imageView: ImageView) {
    imageView.apply {
        setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_undraw_page_not_found_su7k,
                null
            )
        )
    }
}

interface ImageViewCallBack {
    fun onLoadingDone()
}
