package com.ayodkay.apps.swen.notification.jobs

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.annotation.RequiresApi
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.notification.Notification
import com.ayodkay.apps.swen.view.main.MainActivity.Companion.startJobScheduler
import com.ayodkay.apps.swen.view.main.MainActivity.Companion.stopJobScheduler
import java.util.*

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class GetTimeJob : JobService() {
    private var jobParameters: JobParameters? = null

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        this.jobParameters = jobParameters

        val rightNow: Calendar = Calendar.getInstance()
        val currentHourIn24Format: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        val currentMin: Int = rightNow.get(Calendar.MINUTE)
        val currentSec: Int = rightNow.get(Calendar.SECOND)

        if (arrayListOf(
                9,
                13,
                18,
                22
            ).contains(currentHourIn24Format) && currentMin == 0 && currentSec == 0
        ) {
            stopJobScheduler()
            Notification(this).sendEngageNotification(getString(R.string.news_update))
        } else {
            startJobScheduler()
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        return false
    }
}