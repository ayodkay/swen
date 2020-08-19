package com.ayodkay.apps.swen.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.RED
import androidx.core.app.NotificationCompat
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.view.main.MainActivity
import com.facebook.appevents.AppEventsLogger

class Notification internal constructor(private val context: Context){

    companion object{
        private const val UPDATE_NOTIFICATION = 101
        private val  CHANNEL_ID = context.getString(R.string.notification_id)

        private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun deleteNotification(){
            notificationManager.cancel(
                UPDATE_NOTIFICATION
            )
        }


    }


    internal fun sendNotification(message:String){
        AppEventsLogger.newLogger(App.context).logEvent("sentNotification")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context,
            CHANNEL_ID
        )
        val bigIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)


        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O->{
                val name = context.getString(R.string.notification_name)
                val description = context.getString(R.string.notification_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                channel.setShowBadge(true)
                channel.enableLights(true)
                channel.lightColor = RED
                notificationManager.createNotificationChannel(channel)

                builder
                    .setLargeIcon(bigIcon)
                    .setContentTitle(message)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                notificationManager.notify(
                    UPDATE_NOTIFICATION, builder.build())
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.O-> {
                builder
                    .setLargeIcon(bigIcon)
                    .setContentTitle(message)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                notificationManager.notify(
                    UPDATE_NOTIFICATION, builder.build())
            }
        }
    }

}