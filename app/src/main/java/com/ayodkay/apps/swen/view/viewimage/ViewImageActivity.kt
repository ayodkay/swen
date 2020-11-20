package com.ayodkay.apps.swen.view.viewimage

import android.content.res.Configuration
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image.*


class ViewImageActivity : AppCompatActivity() {
    private var scaleGestureDetector:ScaleGestureDetector? = null
    var mScaleFactor = 1.0f
    private var imageView: ImageView? = null

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                background.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val image = intent?.extras?.get("image") as String
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        imageView = findViewById(R.id.newsImage)

        try {
            Picasso.get().load(image).into(imageView, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    imageView?.apply {
                        setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_undraw_page_not_found_su7k,
                                null
                            )
                        )
                    }

                }

            })
        } catch (e: Exception) {
            imageView?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_undraw_page_not_found_su7k,
                    null
                )
            )
        }
    }

    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        pinch.visibility = View.GONE
        scaleGestureDetector?.onTouchEvent(motionEvent)
        return true
    }

    inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f))
            imageView?.scaleX = mScaleFactor
            imageView?.scaleY = mScaleFactor
            return true
        }
    }
}