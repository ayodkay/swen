package com.ayodkay.apps.swen.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color.RED
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.Data
import com.ayodkay.apps.swen.App.Companion.scheduleNotification
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.work.NotifyWork
import com.ayodkay.apps.swen.view.main.MainActivity
import com.squareup.picasso.Picasso
import kotlin.random.Random
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Notification internal constructor() : KoinComponent {
    val context: Context by inject()

    companion object : KoinComponent {
        val context: Context by inject()
        private const val ENGAGE_NOTIFICATION = 101
        private val COUNTRY_NOTIFICATION = Random.nextInt(0, 1000000)
        private val CHANNEL_ID = context.getString(R.string.notification_id)
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun deleteNotification() {
        notificationManager.cancel(
            ENGAGE_NOTIFICATION
        )
    }

    internal fun sendUpdateNotification(message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = getActivity(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
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
            ENGAGE_NOTIFICATION, builder.build()
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

    internal fun sendEngageNotification(message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            getActivity(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
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
            ENGAGE_NOTIFICATION, builder.build()
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

        val data = Data.Builder().putInt(NotifyWork.NOTIFICATION_ID, 0).build()
        scheduleNotification(data, context)
    }

    internal fun sendCountryNotification(
        title: String,
        descriptions: String,
        url: String,
        image: String,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }.putExtra("url", url).putExtra("toMain", true)

        val pendingIntent: PendingIntent =
            getActivity(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        val builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
        val bigIcon = AppCompatResources.getDrawable(context, R.drawable.ic_logo)?.toBitmap()

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

        try {
            val bitmap = Picasso.get().load(image).get()
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        } catch (e: Exception) {
            val bitmap =
                drawableToBitmap(ContextCompat.getDrawable(context, R.drawable.ic_logo_white)!!)
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
        }

        notificationManager.notify(
            COUNTRY_NOTIFICATION, builder.build()
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

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = bitmap?.let { Canvas(it) }
        if (canvas != null) {
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }
}
