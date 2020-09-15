package com.ayodkay.apps.swen.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.App
import com.ayodkay.apps.swen.helper.App.Companion.context
import com.ayodkay.apps.swen.view.WebView
import com.ayodkay.apps.swen.view.main.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.appevents.AppEventsLogger
import com.squareup.picasso.Picasso


class Notification internal constructor(private val context: Context) {

    companion object {
        private const val UPDATE_NOTIFICATION = 101
        private val CHANNEL_ID = context.getString(R.string.notification_id)

        private val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun deleteNotification() {
            notificationManager.cancel(
                UPDATE_NOTIFICATION
            )
        }


    }


    internal fun sendEngageNotification(message: String) {
        AppEventsLogger.newLogger(App.context).logEvent("sentNotification")
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
        val bigIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)

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
            UPDATE_NOTIFICATION, builder.build()
        )

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val name = context.getString(R.string.notification_name)
                val description = context.getString(R.string.notification_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                channel.setShowBadge(true)
                channel.enableLights(true)
                channel.lightColor = RED
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    internal fun sendCountryNotification(title: String, descriptions: String, url: String,
                                         image: String) {
        AppEventsLogger.newLogger(App.context).logEvent("sentCountryNotification")
        val intent = Intent(context, WebView::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK

        }.putExtra("url", url)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
        val bigIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)

        builder
            .setLargeIcon(bigIcon)
            .setContentTitle(title)
            .setContentText(descriptions)
            .setSmallIcon(R.drawable.ic_logo)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        val bitmap = Picasso.get().load(image).get()
        builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        notificationManager.notify(
            UPDATE_NOTIFICATION, builder.build()
        )


        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val name = context.getString(R.string.notification_name)
                val description = context.getString(R.string.notification_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance)
                channel.description = description
                channel.setShowBadge(true)
                channel.enableLights(true)
                channel.lightColor = RED
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}