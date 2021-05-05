package com.ayodkay.apps.swen.view.viewnews

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.view.main.MainActivity
import com.ayodkay.apps.swen.view.viewimage.ViewImageActivity
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_viewnews.*
import kotlinx.android.synthetic.main.more.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewNewActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_viewnews)
        setSupportActionBar(findViewById(R.id.detail_toolbar))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initWindow()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image = intent?.extras?.get("image") as String
        val title = intent?.extras?.get("title") as String
        val source = intent?.extras?.get("source") as String

        val bottomSheet: View = findViewById(R.id.bottomSheet)
        bottomSheet.fitsSystemWindows = false

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    more.visibility = View.VISIBLE
                    swipe_up.visibility = View.VISIBLE
                    swipe_down.visibility = View.GONE

                } else {
                    more.visibility = View.GONE
                    swipe_up.visibility = View.GONE
                    swipe_down.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                if (title.contains("- ")) {
                    loadMore(title.substringAfter("- "))
                } else {
                    loadMore(source)
                }
            }
        }

        try {
            Picasso.get().load(image).into(dImage, object : Callback {
                override fun onSuccess() {
                    progress.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    progress.visibility = View.GONE
                    dImage.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_undraw_page_not_found_su7k,
                            null
                        )
                    )

                }

            })
        } catch (e: Exception) {
            progress.visibility = View.GONE
            dImage.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_undraw_page_not_found_su7k,
                    null
                )
            )
        }

        dImage.setOnClickListener {
            startActivity(
                Intent(this, ViewImageActivity::class.java)
                    .putExtra("image", image)
            )
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.item_detail_container, ViewFragment())
                .commit()
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


    private fun loadMore(query: String) {
        val newViewModel = ViewModelProvider(this).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()
        newViewModel.getEveryThingFromRepo(
            pageSize = 100,
            q = query, sort_by = "publishedAt"
        ).observe(this, {
            moreBy.apply {
                layoutManager = LinearLayoutManager(context)
                hasFixedSize()
                articleArrayList.addAll(it.articles)
                adapter = AdsRecyclerView(
                    articleArrayList,
                    this@ViewNewActivity,
                    this@ViewNewActivity
                )
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {

                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                navigateUpTo(Intent(this, MainActivity::class.java))

                true
            }
            else -> super.onOptionsItemSelected(item)
        }


}