package com.ayodkay.apps.swen.view


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.view.main.MainActivity
import com.facebook.appevents.AppEventsLogger
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash_screen.*


private const val MY_REQUEST_CODE = 1
private var TAG = SplashScreen::class.java.name

class SplashScreen : AppCompatActivity() {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)


    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    nextActivity()
                }
            }
    }

    private val listener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                updating.visibility = View.GONE
                Toast.makeText(this, getString(R.string.updated), Toast.LENGTH_LONG).show()
                nextActivity()

            }

            if (installState.installStatus() == InstallStatus.DOWNLOADING) {
                updating.visibility = View.VISIBLE
                Toast.makeText(this, getString(R.string.updating), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    AppEventsLogger.newLogger(context).logEvent("dynamicLink")
                    startActivity(Intent(this, WebView::class.java)
                        .putExtra("url",pendingDynamicLinkData.link.toString())
                        .putExtra("toMain", true))

                    finish()
                }else{
                    // Returns an intent object that you use to check for an update.
                    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
                    // Checks that the platform will allow the specified type of update.
                    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                            AppEventsLogger.newLogger(context).logEvent("in-appUpdate")

                            appUpdateManager.registerListener(listener)
                            appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.FLEXIBLE,
                                // The current activity making the update request.
                                this,
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE
                            )
                        }else{
                            nextActivity()
                        }
                    }.addOnFailureListener {
                        nextActivity()
                    }

                }
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

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
    private fun nextActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            appUpdateManager.unregisterListener(listener)
            val db = Helper.getCountryDatabase(this)
            if (db.countryDao().getAll() != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, AskLocation::class.java))
                finish()
            }
        }, 1000)
    }
}