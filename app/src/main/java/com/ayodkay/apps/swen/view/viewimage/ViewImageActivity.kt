package com.ayodkay.apps.swen.view.viewimage

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image.*


class ViewImageActivity : AppCompatActivity() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initWindow()
        }
        val image = intent?.extras?.get("image") as String
        imageView = findViewById(R.id.newsImage)

        try {
            Picasso.get().load(image).into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView?.apply {
                        setOnClickListener {
                            pinch.visibility = View.GONE
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    imageView?.apply {
                        setOnClickListener {
                            pinch.visibility = View.GONE
                        }
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

            imageView?.apply {

                setOnClickListener {
                    pinch.visibility = View.GONE
                }
                setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_undraw_page_not_found_su7k,
                        null
                    )
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initWindow() {
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            }

        }
    }
}