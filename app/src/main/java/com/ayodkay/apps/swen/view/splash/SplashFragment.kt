package com.ayodkay.apps.swen.view.splash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.ayodkay.apps.swen.databinding.FragmentSplashBinding
import com.ayodkay.apps.swen.helper.BaseFragment
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.extentions.isNotNull
import com.facebook.appevents.AppEventsLogger
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

private const val MY_REQUEST_CODE = 1
private val TAG = SplashFragment::class.java.name

class SplashFragment : BaseFragment() {
    private val splashViewModel: SplashViewModel by viewModels()
    override fun onResume() {
        super.onResume()
        splashViewModel.appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    nextActivity()
                }
            }
            .addOnFailureListener {
                nextActivity()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FragmentSplashBinding.inflate(layoutInflater, container, false).apply {
        viewModel = splashViewModel
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val link = requireActivity().intent?.extras?.get("url")
        val isPush = requireActivity().intent?.extras?.get("isPush")
        if (isPush.isNotNull() && link.toString().isNotEmpty()) {
            navigateTo(
                SplashFragmentDirections.actionNavSplashToNavWebView(
                    link = link.toString(), navigateToMain = true
                )
            )
        } else {
            Firebase.dynamicLinks
                .getDynamicLink(requireActivity().intent)
                .addOnSuccessListener(requireActivity()) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    if (pendingDynamicLinkData != null) {
                        AppEventsLogger.newLogger(requireContext()).logEvent("dynamicLink")
                        navigateTo(
                            SplashFragmentDirections.actionNavSplashToNavWebView(
                                link = pendingDynamicLinkData.link.toString(),
                                navigateToMain = true
                            )
                        )
                    } else {
                        // Returns an intent object that you use to check for an update.
                        val appUpdateInfoTask = splashViewModel.appUpdateManager.appUpdateInfo
                        // Checks that the platform will allow the specified type of update.
                        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                            if (appUpdateInfo.updateAvailability() ==
                                UpdateAvailability.UPDATE_AVAILABLE &&
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                            ) {
                                AppEventsLogger
                                    .newLogger(requireContext()).logEvent("in-appUpdate")

                                splashViewModel.appUpdateManager.apply {
                                    registerListener(splashViewModel.listener)
                                    startUpdateFlowForResult(
                                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                        appUpdateInfo,
                                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                        AppUpdateType.FLEXIBLE,
                                        // The current activity making the update request.
                                        requireActivity(),
                                        // Include a request code to later monitor this update request.
                                        MY_REQUEST_CODE
                                    )
                                }
                            } else {
                                nextActivity()
                            }
                        }.addOnFailureListener {
                            nextActivity()
                        }
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    Log.w(TAG, "getDynamicLink:onFailure", e)
                }
        }

        splashViewModel.navigateEvent.observe(viewLifecycleOwner) {
            nextActivity()
        }

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
                nextActivity()
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun nextActivity() {
        val db = Helper.getCountryDatabase(requireContext())
        if (db.countryDao().getAll() != null) {
            navigateTo(SplashFragmentDirections.actionNavSplashToNavMainSwen())
        } else {
            navigateTo(SplashFragmentDirections.actionNavSplashToNavLocation())
        }
    }
}
