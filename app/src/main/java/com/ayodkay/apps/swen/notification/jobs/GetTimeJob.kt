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

        if (arrayListOf(8, 12, 15, 18, 21).contains(currentHourIn24Format)) {
            stopJobScheduler()
            if (lastHour != currentHourIn24Format) {
                Notification(this).sendEngageNotification(getString(R.string.news_update))
                lastHour = currentHourIn24Format
            } else {
                startJobScheduler()
            }
        } else {
            startJobScheduler()
        }
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        return false
    }


    companion object {
        var lastHour = 0
    }
}