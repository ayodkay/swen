package com.ayodkay.apps.swen.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.databinding.ActivitySplashScreenBinding
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.work.UpdateWorkManager
import com.ayodkay.apps.swen.view.main.MainActivity
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash_screen.*


private const val MY_REQUEST_CODE = 1
private const val TAG_OUTPUT = "update"
private val TAG = SplashScreen::class.java.name

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private var outputWorkInfos: LiveData<List<WorkInfo>>? = null

    override fun onResume() {
        super.onResume()
        outputWorkInfos?.observe(this, workInfosObserver())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Helper.initializeAds(this, getString(R.string.mopub_adunit_native))
        Helper.initializeAds(this, getString(R.string.mopub_adunit_banner))
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    AppEventsLogger.newLogger(context).logEvent("dynamicLink")
                    startActivity(
                        Intent(this, WebView::class.java)
                            .putExtra("url", pendingDynamicLinkData.link.toString())
                            .putExtra("toMain", true)
                    )

                    finish()
                } else {
                    val workManager = WorkManager.getInstance(this)
                    val uploadWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<UpdateWorkManager>()
                            .addTag(TAG_OUTPUT)
                            .build()
                    workManager.enqueue(uploadWorkRequest)
                    outputWorkInfos =
                        workManager.getWorkInfosByTagLiveData(TAG_OUTPUT) as LiveData<List<WorkInfo>>
                    outputWorkInfos!!.observe(this, workInfosObserver())
                }
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                nextActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                nextActivity()
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun nextActivity() {
        val db = Helper.getCountryDatabase(this)
        if (db.countryDao().getAll() != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, AskLocation::class.java))
            finish()
        }
    }
}