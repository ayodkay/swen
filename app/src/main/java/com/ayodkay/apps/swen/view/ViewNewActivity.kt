package com.ayodkay.apps.swen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.NewsApiClient
import com.ayodkay.apps.swen.helper.NewsApiClient.Companion.getEverything
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.viewmodel.NewsViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.more.*
import kotlinx.android.synthetic.main.news_card.*
import java.io.ByteArrayOutputStream
class ViewNewActivity : AppCompatActivity() {

    private lateinit var shareNews:Intent

    override fun onStart() {
        super.onStart()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                setTheme(R.style.AppThemeNight)
                background.setBackgroundColor(ContextCompat.getColor(this,R.color.background))
            } // Night mode is active, we're using dark theme
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_card)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

        var dynamicLink = ""

        val url = intent?.extras?.get("url") as String
        val image = intent?.extras?.get("image") as String
        val title = intent?.extras?.get("title") as String
        val content = intent?.extras?.get("content") as String
        val description = intent?.extras?.get("description") as String
        val source = intent?.extras?.get("source") as String
        val imageView = findViewById<ImageView>(R.id.dImage)
        val contentTextView = findViewById<TextView>(R.id.content)
        val titleTextView = findViewById<TextView>(R.id.dTitle)
        val adView = findViewById<AdView>(R.id.adView)
        val sourceTextView = findViewById<TextView>(R.id.dSource)
        val shareView = findViewById<RelativeLayout>(R.id.shareView)
        val article = findViewById<MaterialButton>(R.id.full_article)

        val bottomSheet:View = findViewById(R.id.bottomSheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false

        val talky = TextToSpeech(this){}

        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(url)
            domainUriPrefix = getString(R.string.domainUriPrefix)
            androidParameters{}
        }.addOnSuccessListener { result ->
            dynamicLink = result.shortLink.toString()
        }.addOnFailureListener {}

        val newsViewModel: NewsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)

        val newsApiClient = NewsApiClient()

        if (title.contains("- ")){
            loadMore(newsViewModel,newsApiClient,title.substringAfter("- "))
        }else{
            loadMore(newsViewModel,newsApiClient,source)
        }

        shareView.setOnClickListener {
            if (!image.isBlank()){
                Picasso.get().load(image).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        shareNews = Intent(Intent.ACTION_SEND)
                        shareNews.type = "image/jpeg"
                        shareNews.putExtra(
                            Intent.EXTRA_TEXT,
                            "${title}\n${dynamicLink}"
                        )
                        try {
                            val uri = getImageUri(bitmap)
                            shareNews.putExtra(
                                Intent.EXTRA_STREAM, uri
                            )
                            startActivity(
                                Intent.createChooser(
                                    shareNews,
                                    getString(R.string.share_news)
                                )
                            )
                        } catch (e: Exception) {
                            checkPermission()
                        }
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(
                        e: java.lang.Exception?,
                        errorDrawable: Drawable?
                    ) {
                    }
                })
            }else{
                shareNews = Intent(Intent.ACTION_SEND)
                shareNews.type = "text/plain"
                shareNews.putExtra(
                    Intent.EXTRA_TEXT,
                    "\n\n${title}\n${dynamicLink}"
                )

                startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playView.setOnClickListener {
                talky.speak(title+content,TextToSpeech.QUEUE_FLUSH,null, null)

            }
        }else{
            playView.visibility = View.GONE
        }



        article.setOnClickListener {
            startActivity(
                Intent(this, WebView::class.java)
                    .putExtra("url", url)
                    .putExtra("toMain", false)
            )
        }

        adView.loadAd(adRequest)


        if (content.isBlank()) {
            contentTextView.text = description
        } else {
            contentTextView.text = content

        }
        titleTextView.text = title
        sourceTextView.text = source

        try {
            Picasso.get().load(image).into(imageView, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception?) {
                    imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_undraw_page_not_found_su7k,
                            null
                        )
                    )

                }

            })
        } catch (e: Exception) {
            imageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_undraw_page_not_found_su7k,
                    null
                )
            )
        }
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
         val path: String = MediaStore.Images.Media.insertImage(
             contentResolver,
             inImage,
             "Title",
             null
         )
        return Uri.parse(path)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
    }

    private fun checkPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {

            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE
                    )
                }
            }
        }
    }


    private fun loadMore(newsViewModel:NewsViewModel,newsApiClient:NewsApiClient,query:String){
        newsViewModel.getNews(getEverything(newsApiClient, pageSize = 100,
            q = query,sort_by = "publishedAt"))
            .observe(this, Observer {
                moreBy.apply {
                    layoutManager = LinearLayoutManager(this@ViewNewActivity)
                    hasFixedSize()
                    adapter = AdsRecyclerView(
                        Helper.handleJson(it),
                        this@ViewNewActivity,
                        this@ViewNewActivity,
                        this@ViewNewActivity
                    )
                }
            })
    }

}