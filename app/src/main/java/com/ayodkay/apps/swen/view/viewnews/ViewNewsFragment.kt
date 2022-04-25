package com.ayodkay.apps.swen.view.viewnews

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.applovin.mediation.MaxAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivityViewnewsBinding
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.adapter.MaxAdsRecyclerView
import com.ayodkay.apps.swen.view.WebView
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.more.*
import java.io.ByteArrayOutputStream
import java.util.*

class ViewNewsFragment : Fragment() {
    private lateinit var shareNews: Intent
    private lateinit var nativeAdLoader: MaxNativeAdLoader
    private var nativeAd: MaxAd? = null
    var talky: TextToSpeech? = null

    private val viewNewsViewModel: ViewNewsViewModel by viewModels()
    private val args: ViewNewsFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ActivityViewnewsBinding.inflate(inflater, container, false).apply {
        viewModel = viewNewsViewModel
        viewNewsViewModel.source = args.source
        viewNewsViewModel.url = args.url
        viewNewsViewModel.image = args.image
        viewNewsViewModel.title = args.title
        viewNewsViewModel.content = args.content
        viewNewsViewModel.description = args.description
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewNewsViewModel.loadAd.set(true)

        if (viewNewsViewModel.languageCode.get() != "und") {
            talky = TextToSpeech(requireContext()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result: Int = talky!!.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        AppLog.l("TTS--> Language not supported")
                    } else {
                        talky!!.language = viewNewsViewModel.languageCode.get()?.let { Locale(it) }
                        talky!!.setSpeechRate(0.8f)
                        viewNewsViewModel.showPlayButton.set(true)
                        talky!!.setOnUtteranceProgressListener(object :
                            UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                viewNewsViewModel.showPlayButton.set(false)
                                viewNewsViewModel.showStopButton.set(true)
                            }

                            override fun onDone(utteranceId: String?) {
                                viewNewsViewModel.showPlayButton.set(true)
                                viewNewsViewModel.showStopButton.set(false)
                            }

                            override fun onError(utteranceId: String?) {}
                        })
                    }
                } else {
                    AppLog.l("TTS--> Initialization failed $status")
                }
            }
        }

        viewNewsViewModel.playEvent.observe(viewLifecycleOwner) {
            talky!!.speak("${viewNewsViewModel.title}. ${viewNewsViewModel.content}.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)
        }

        viewNewsViewModel.stopEvent.observe(viewLifecycleOwner) {
            talky!!.stop()
        }

        viewNewsViewModel.shareEvent.observe(viewLifecycleOwner) {
            if (viewNewsViewModel.image.isNotBlank()) {
                Picasso.get().load(viewNewsViewModel.image).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                        shareNews = Intent(Intent.ACTION_SEND)
                        shareNews.type = "image/jpeg"
                        shareNews.putExtra(
                            Intent.EXTRA_TEXT,
                            "${viewNewsViewModel.title}\n${viewNewsViewModel.dynamicLink}"
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
                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                })
            } else {
                shareNews = Intent(Intent.ACTION_SEND)
                shareNews.type = "text/plain"
                shareNews.putExtra(
                    Intent.EXTRA_TEXT,
                    "\n\n${viewNewsViewModel.title}\n${viewNewsViewModel.dynamicLink}"
                )

                startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
            }
        }

        viewNewsViewModel.fullArticleEvent.observe(viewLifecycleOwner) {
            startActivity(Intent(context, WebView::class.java)
                .putExtra("url", viewNewsViewModel.url)
                .putExtra("toMain", false))
        }


        if (viewNewsViewModel.title.contains("- ")) {
            loadMore(viewNewsViewModel.title.substringAfter("- "))
        } else {
            loadMore(viewNewsViewModel.source)
        }

    }

    companion object {
        private const val REQUEST_CODE = 101
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


    override fun onDestroy() {
        if (talky != null) {
            talky?.stop()
            talky?.shutdown()
        }
        // Must destroy native ad or else there will be memory leaks.
        if (nativeAd != null) {
            // Call destroy on the native ad from any native ad loader.
            nativeAdLoader.destroy(nativeAd)
        }

        // Destroy the actual loader itself
        nativeAdLoader.destroy()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initWindow() {
    }


    private fun loadMore(query: String) {

        val everythingBuilder = EverythingBuilder.Builder()
            .q(query)
            .sortBy("publishedAt")
            .pageSize(100)
            .build()

        with(Helper.setUpNewsClient(requireActivity())) {
            getEverything(everythingBuilder, object : ArticlesLiveDataResponseCallback {
                override fun onFailure(throwable: Throwable) {
                    viewNewsViewModel.showBottomSheet.set(false)
                }

                override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                    response.observe(viewLifecycleOwner) { newsResponse ->
                        AppLog.l(newsResponse)
                        if (newsResponse.totalResults == 0) {
                            viewNewsViewModel.showBottomSheet.set(false)
                        } else {
                            viewNewsViewModel.showBottomSheet.set(true)
                            viewNewsViewModel.moreNews.addAll(newsResponse.articles)

                            moreBy.apply {
                                layoutManager = LinearLayoutManager(requireContext())
                                hasFixedSize()
                                MaxAdsRecyclerView(viewNewsViewModel.moreNews,
                                    this@ViewNewsFragment,
                                    requireContext(), nativeAdLoader, nativeAd)
                            }
                        }

                    }
                }

            })
        }
    }
}