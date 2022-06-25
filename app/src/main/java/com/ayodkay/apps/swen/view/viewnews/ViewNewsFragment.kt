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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentViewNewsBinding
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.helper.BaseFragment
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.extentions.ifNull
import com.ayodkay.apps.swen.helper.room.bookmarks.BookmarkRoomVM
import com.github.ayodkay.builder.EverythingBuilder
import com.github.ayodkay.models.ArticleResponse
import com.github.ayodkay.mvvm.interfaces.ArticlesLiveDataResponseCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.ByteArrayOutputStream
import java.util.*

class ViewNewsFragment : BaseFragment() {
    var talky: TextToSpeech? = null
    private val viewNewsViewModel: ViewNewsViewModel by viewModels()
    private val args: ViewNewsFragmentArgs by navArgs()
    lateinit var binding: FragmentViewNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentViewNewsBinding.inflate(inflater, container, false).apply {
        viewModel = viewNewsViewModel
        with(viewNewsViewModel) {
            source = args.source
            url = args.url
            image = args.image
            title = args.title
            content = args.content.replace(regex = Regex("<.*?>"), "")
                .replace(regex = Regex("\\W+\\d+ chars\\W"), "...")
                .trim()
            description = args.description.replace(regex = Regex("<.*?>"), "").trim()
            bookMarkRoom.set(ViewModelProvider(this@ViewNewsFragment)[BookmarkRoomVM::class.java])
            setUpLanguageIdentify()
        }
        binding = this
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.detailToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewNewsViewModel.loadAd.set(true)
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        onBackPressed {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    navigateUp()
                }
                else -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    viewNewsViewModel.isCollapsed
                        .set(newState == BottomSheetBehavior.STATE_COLLAPSED)
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

        if (viewNewsViewModel.languageCode.get() != "und") {
            talky = TextToSpeech(requireContext()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result: Int = talky!!.setLanguage(Locale.ENGLISH)
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        AppLog.l("TTS--> Language not supported")
                    } else {
                        talky!!.language = viewNewsViewModel.languageCode.get()?.let { Locale(it) }
                        talky!!.setSpeechRate(0.8f)
                        talky!!.setOnUtteranceProgressListener(object :
                                UtteranceProgressListener() {
                                override fun onStart(utteranceId: String?) {
                                    viewNewsViewModel.isPlaying.set(true)
                                    viewNewsViewModel.isTalkingDrawable.set(
                                        R.drawable.ic_baseline_stop_24
                                    )
                                }

                                override fun onDone(utteranceId: String?) {
                                    viewNewsViewModel.isPlaying.set(false)
                                    viewNewsViewModel.isTalkingDrawable.set(
                                        R.drawable.ic_baseline_play_arrow_24
                                    )
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
            with(viewNewsViewModel) {
                if (isPlaying.get() == true) {
                    talky!!.stop()
                    viewNewsViewModel.isTalkingDrawable.set(
                        R.drawable.ic_baseline_play_arrow_24
                    )
                    viewNewsViewModel.isPlaying.set(false)
                } else {
                    talky!!.speak(
                        "$title. ${content.ifNull { description }}.",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                    )
                }
            }
        }

        viewNewsViewModel.shareEvent.observe(viewLifecycleOwner) {
            val shareNews = Intent(Intent.ACTION_SEND)

            if (viewNewsViewModel.image.isNotBlank()) {
                Picasso.get().load(viewNewsViewModel.image).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {

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
                shareNews.type = "text/plain"
                shareNews.putExtra(
                    Intent.EXTRA_TEXT,
                    "\n\n${viewNewsViewModel.title}\n${viewNewsViewModel.dynamicLink}"
                )

                startActivity(Intent.createChooser(shareNews, getString(R.string.share_news)))
            }
        }

        viewNewsViewModel.fullArticleEvent.observe(viewLifecycleOwner) {
            navigateTo(
                ViewNewsFragmentDirections.actionNavViewNewsToNavWebView(
                    link = viewNewsViewModel.url,
                    navigateToMain = false
                )
            )
        }

        if (viewNewsViewModel.title.contains("- ")) {
            loadMore(viewNewsViewModel.title.substringAfter("- "))
        } else {
            loadMore(viewNewsViewModel.source)
        }

        viewNewsViewModel.viewImageEvent.observe(viewLifecycleOwner) {
            navigateTo(
                ViewNewsFragmentDirections.actionNavViewNewsToNavViewImage(
                    image = viewNewsViewModel.image
                )
            )
        }

        viewNewsViewModel.goToViewNewsFragment.observe(viewLifecycleOwner) {
            navigateTo(
                ViewNewsFragmentDirections.actionNavViewNewsSelf(
                    source = it.source.name.ifNull { "" }, url = it.url.ifNull { "" },
                    image = it.urlToImage.ifNull { "" }, title = it.title.ifNull { "" },
                    content = it.content.ifNull { it.description.ifNull { "" } },
                    description = it.description.ifNull { "" }
                )
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (talky != null) {
            talky?.stop()
            talky?.shutdown()
        }
    }

    private fun loadMore(query: String) {

        val everythingBuilder = EverythingBuilder.Builder()
            .q(query)
            .sortBy("publishedAt")
            .pageSize(100)
            .build()

        with(Helper.setUpNewsClient(requireActivity())) {
            getEverything(
                everythingBuilder,
                object : ArticlesLiveDataResponseCallback {
                    override fun onFailure(throwable: Throwable) {
                        viewNewsViewModel.showBottomSheet.set(false)
                    }

                    override fun onSuccess(response: MutableLiveData<ArticleResponse>) {
                        response.observeForever { newsResponse ->
                            AppLog.l(newsResponse)
                            if (newsResponse.totalResults == 0) {
                                viewNewsViewModel.showBottomSheet.set(false)
                            } else {
                                viewNewsViewModel.showBottomSheet.set(true)
                                viewNewsViewModel.moreNews.addAll(newsResponse.articles)
                            }
                        }
                    }
                }
            )
        }
    }
}
