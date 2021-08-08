package com.ayodkay.apps.swen.view.viewnews

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentViewnewsBinding
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.view.WebView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.ByteArrayOutputStream
import java.util.*

class ViewFragment : Fragment() {
    private lateinit var shareNews: Intent
    var talky: TextToSpeech? = null

    private var _binding: FragmentViewnewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title =
            activity?.intent?.extras?.get("source") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentViewnewsBinding.inflate(inflater, container, false)

        MobileAds.initialize(requireContext())
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        var dynamicLink = ""

        val url = activity?.intent?.extras?.get("url") as String
        val image = activity?.intent?.extras?.get("image") as String
        val title = activity?.intent?.extras?.get("title") as String
        val content = (activity?.intent?.extras?.get("content") as String)
            .replace(regex = Regex("<.*?>"), "")
            .replace(regex = Regex("\\W+\\d+ chars\\W"), "...")
            .trim()
        val description = (activity?.intent?.extras?.get("description") as String)
            .replace(regex = Regex("<.*?>"), "").trim()


        val contentTextView = binding.content
        val titleTextView = binding.dTitle
        val shareView = binding.shareView
        val article = binding.fullArticle
        val playView = binding.playView
        val stopView = binding.stopView

        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(title)
            .addOnSuccessListener { languageCode ->
                if (languageCode != "und") {
                    talky = TextToSpeech(requireContext()) { status ->
                        if (status == TextToSpeech.SUCCESS) {
                            val result: Int = talky!!.setLanguage(Locale.GERMAN)
                            if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED
                            ) {
                                AppLog.l("TTS--> Language not supported")
                            } else {
                                talky!!.language = Locale(languageCode)
                                talky!!.setSpeechRate(0.8f)
                                playView.visibility = View.VISIBLE
                                playView.setOnClickListener {
                                    talky!!.speak("$title. $content",
                                        TextToSpeech.QUEUE_FLUSH,
                                        null,
                                        TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)

                                }

                                stopView.setOnClickListener {
                                    talky!!.stop()
                                    stopView.visibility = View.GONE
                                    playView.visibility = View.VISIBLE
                                }

                                talky!!.setOnUtteranceProgressListener(object :
                                    UtteranceProgressListener() {
                                    override fun onStart(utteranceId: String?) {
                                        requireActivity().runOnUiThread {
                                            playView.visibility = View.GONE
                                            stopView.visibility = View.VISIBLE
                                        }
                                    }

                                    override fun onDone(utteranceId: String?) {
                                        requireActivity().runOnUiThread {
                                            playView.visibility = View.VISIBLE
                                            stopView.visibility = View.GONE
                                        }
                                    }

                                    override fun onError(utteranceId: String?) {}

                                })
                            }
                        } else {
                            AppLog.l("TTS--> Initialization failed $status")
                        }
                    }
                }
            }.addOnFailureListener {}


        shareView.setOnClickListener {
            Firebase.dynamicLinks.shortLinkAsync {
                link = Uri.parse(url)
                domainUriPrefix = getString(R.string.domainUriPrefix)
                androidParameters {}
            }.addOnSuccessListener { result ->
                dynamicLink = result.shortLink.toString()
            }.addOnFailureListener {}
            if (image.isNotBlank()) {
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
                        errorDrawable: Drawable?,
                    ) {
                    }
                })
            } else {
                shareNews = Intent(Intent.ACTION_SEND)
                shareNews.type = "text/plain"
                shareNews.putExtra(
                    Intent.EXTRA_TEXT,
                    "\n\n${title}\n${dynamicLink}"
                )

                startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
            }
        }

        article.setOnClickListener {
            startActivity(
                Intent(context, WebView::class.java)
                    .putExtra("url", url)
                    .putExtra("toMain", false)
            )
        }

        if (content.isBlank()) {
            contentTextView.text = description
        } else {
            contentTextView.text = content

        }
        titleTextView.text = title
        return binding.root
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context?.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun checkPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            -> {

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
                } else {
                    Toast.makeText(
                        context,
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

    override fun onDestroy() {
        if (talky != null) {
            talky?.stop()
            talky?.shutdown()
        }
        super.onDestroy()
    }

}