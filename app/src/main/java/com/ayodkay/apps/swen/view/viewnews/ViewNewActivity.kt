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
import androidx.lifecycle.MutableLiveData
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivityViewnewsBinding
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.Helper.setUpNewsClient
import com.ayodkay.apps.swen.view.main.MainActivity
import com.ayodkay.apps.swen.view.viewimage.ViewImageActivity
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.more.*

class ViewNewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewnewsBinding
    override fun onStart() {
        super.onStart()
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                binding.background.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.background
                    )
                )
            } // Night mode is active, we're using dark theme
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewnewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.detailToolbar)

        initWindow()

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val image = intent?.extras?.get("image") as String
        val title = intent?.extras?.get("title") as String
        val source = intent?.extras?.get("source") as String

        binding.bottomSheet.fitsSystemWindows = false

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

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

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        runOnUiThread {
            if (title.contains("- ")) {
                loadMore(title.substringAfter("- "))
            } else {
                loadMore(source)
            }
            try {
                Picasso.get().load(image).into(binding.dImage, object : Callback {
                    override fun onSuccess() {
                        binding.progress.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        binding.progress.visibility = View.GONE
                        binding.dImage.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_undraw_page_not_found_su7k,
                                null
                            )
                        )

                    }

                })
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.dImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_undraw_page_not_found_su7k,
                        null
                    )
                )
            }
        }

        binding.dImage.setOnClickListener {
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
        val newsResponseList = arrayListOf<Article>()

        val everythingBuilder = EverythingBuilder.Builder()
            .q(query)
            .sortBy("publishedAt")
            .pageSize(100)
            .build()

        with(setUpNewsClient(this)) {
            getEverything(everythingBuilder, object : ArticlesLiveDataResponseCallback {
                override fun onFailure(throwable: Throwable) {
                    binding.bottomSheet.visibility = View.GONE
                }

                override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                    response.observe(this@ViewNewActivity) { newsResponse ->
                        AppLog.l(newsResponse)
                        if (newsResponse.totalResults == 0) {
                            binding.bottomSheet.visibility = View.GONE
                        } else {
                            binding.bottomSheet.visibility = View.VISIBLE
//                            moreBy.apply {
//                                layoutManager = LinearLayoutManager(this@ViewNewActivity)
//                                hasFixedSize()
//                                newsResponseList.addAll(newsResponse.articles)
//                                val desiredAssets = EnumSet.of(
//                                    NativeAdAsset.TITLE,
//                                    NativeAdAsset.TEXT,
//                                    NativeAdAsset.ICON_IMAGE,
//                                    NativeAdAsset.MAIN_IMAGE,
//                                    NativeAdAsset.CALL_TO_ACTION_TEXT,
//                                    NativeAdAsset.SPONSORED
//                                )
//                                val requestParameters = RequestParameters.Builder()
//                                    .desiredAssets(desiredAssets)
//                                    .build()
//                                val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(
//                                    ViewBinder.Builder(R.layout.native_ad_list_item)
//                                        .titleId(R.id.native_title)
//                                        .textId(R.id.native_text)
//                                        .mainImageId(R.id.native_main_image)
//                                        .iconImageId(R.id.native_icon_image)
//                                        .callToActionId(R.id.native_cta)
//                                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
//                                        .sponsoredTextId(R.id.native_sponsored_text_view)
//                                        .build()
//                                )
//
//                                MoPubRecyclerAdapter(
//                                    this@ViewNewActivity, AdMobRecyclerView(
//                                        newsResponseList,
//                                        this@ViewNewActivity,
//                                        this@ViewNewActivity
//                                    )
//                                ).apply {
//                                    registerAdRenderer(moPubStaticNativeAdRenderer)
//                                }.also {
//                                    moreBy.apply {
//                                        it.loadAds(getString(R.string.mopub_adunit_native),
//                                            requestParameters)
//                                        adapter = it
//                                        layoutManager = LinearLayoutManager(this@ViewNewActivity)
//                                    }
//                                }
//                            }
                        }

                    }
                }

            })
        }
    }

    override fun onBackPressed() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            super.onBackPressed()
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
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