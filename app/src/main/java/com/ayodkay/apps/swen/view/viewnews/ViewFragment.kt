package com.ayodkay.apps.swen.view.viewnews

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.FragmentViewnewsBinding
import java.io.ByteArrayOutputStream

class ViewFragment : Fragment() {
    private lateinit var shareNews: Intent
    var talky: TextToSpeech? = null

    private var _binding: FragmentViewnewsBinding? = null
    private val binding get() = _binding!!

    private val viewNewsViewModel: ViewNewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        FragmentViewnewsBinding.inflate(inflater, container, false).apply {
            viewModel = viewNewsViewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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