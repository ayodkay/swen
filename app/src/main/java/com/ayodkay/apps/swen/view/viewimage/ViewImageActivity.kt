package com.ayodkay.apps.swen.view.viewimage

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivityViewImageBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class ViewImageActivity : AppCompatActivity() {
    private var imageView: ImageView? = null

    private lateinit var binding: ActivityViewImageBinding

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                binding.background.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.background))
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val windows = window // in Activity's onCreate() for instance
        windows.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val image = intent.getStringExtra("image")
        imageView = binding.newsImage

        try {
            Picasso.get().load(image).into(imageView, object : Callback {
                override fun onSuccess() {
                    imageView?.apply {
                        setOnClickListener {
                            binding.pinch.visibility = View.GONE
                        }
                    }
                }

                override fun onError(e: Exception?) {
                    imageView?.apply {
                        setOnClickListener {
                            binding.pinch.visibility = View.GONE
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
                    binding.pinch.visibility = View.GONE
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
}