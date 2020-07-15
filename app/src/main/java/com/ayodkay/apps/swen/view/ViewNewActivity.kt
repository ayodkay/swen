package com.ayodkay.apps.swen.view

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ayodkay.apps.swen.R
import com.facebook.share.model.ShareLinkContent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.news_card.*
import java.io.ByteArrayOutputStream

class ViewNewActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {

            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                background.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            } // Night mode is active, we're using dark theme
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_card)

        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

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
        val share = findViewById<ImageView>(R.id.share)
        val article = findViewById<MaterialButton>(R.id.full_article)

        share.setOnClickListener {
            if (image.isBlank()){
                Picasso.get().load(image).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        val shareNews = Intent(Intent.ACTION_SEND)
                        shareNews.type = "image/jpeg"
                        shareNews.putExtra(
                            Intent.EXTRA_TEXT,
                            "${resources.getString(R.string.share_app)} ${resources.getString(R.string.bit_ly)}\n\n${title}\n${url}"
                        )
                        try {
                            val uri = getImageUri(bitmap)
                            shareNews.putExtra(
                                Intent.EXTRA_STREAM,uri
                            )
                            startActivity(Intent.createChooser(shareNews, "Share News"))
                        }catch (e:Exception){
                            checkPermission()
                        }
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                    }
                })
            }else{
                val shareNews = Intent(Intent.ACTION_SEND)
                shareNews.type = "text/plain"
                shareNews.putExtra(
                    Intent.EXTRA_TEXT,
                    "${resources.getString(R.string.share_app)} ${resources.getString(R.string.bit_ly)}\n\n${title}\n${url}"
                )

                startActivity(Intent.createChooser(shareNews, "Share News"))
            }
        }

        val builder = ShareLinkContent.Builder()
            .setContentUrl(Uri.parse(url)).build()

        facebookShare.shareContent = builder

        facebookShare.apply {
            shareContent = builder
            setOnClickListener {
                val clipboard =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData =
                    ClipData
                        .newPlainText("label", "${resources.getString(R.string.share_app)} ${resources.getString(R.string.bit_ly)}\n\n\n\n$title")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@ViewNewActivity, getString(R.string.text_copied), Toast.LENGTH_SHORT).show()

            }
        }

        article.setOnClickListener {
            startActivity(
                Intent(this, WebView::class.java)
                    .putExtra
                        ("url", url)
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
                    imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
                }

            })
        } catch (e: Exception) {
            imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_undraw_page_not_found_su7k))
        }
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        Log.d("TAG", "getImageUri: $inImage")

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
            REQUEST_CODE->{

                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    Toast.makeText(this, "permission granted try sharing again", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "you need accept permission to share", Toast.LENGTH_SHORT)
                        .show()
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
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE)
                }
            }
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getApplicationInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }



}