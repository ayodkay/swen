package com.ayodkay.apps.swen.helper.work

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.Color.RED
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.graphics.drawable.toBitmap
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.view.WebView
import com.ayodkay.apps.swen.view.main.MainActivity
import com.github.ayodkay.builder.TopHeadlinesBuilder
import com.github.ayodkay.client.NewsApiClient
import com.github.ayodkay.interfaces.ArticlesResponseCallback
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.ArticleResponse
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


class NotifyWork(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        getNews(context)
        return success()
    }

    private fun setUpNewsClient(): NewsApiClient {
        return NewsApiClient("62238ad0d1244443b9bfbad61fea89da")
    }

    private fun getNews(context: Context) {
        val db = Helper.getCountryDatabase(context)
        val country = try {
            db.countryDao().getAll().country
        } catch (e: Exception) {
            "us"
        }
        val category = arrayOf(
            "general", "entertainment", "sports", "business", "health", "science", "technology",
        ).random()
        val newsApiClientWithObserver = setUpNewsClient()
        val topHeadlinesBuilder = TopHeadlinesBuilder.Builder()
            .country(country)
            .category(category)
            .pageSize(1)
            .build()

        newsApiClientWithObserver.getTopHeadlines(topHeadlinesBuilder,
            object : ArticlesResponseCallback {
                val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
                override fun onFailure(throwable: Throwable) {
                    sendNotification(id, context.getString(R.string.news_update))
                }

                override fun onSuccess(response: ArticleResponse) {
                    if (response.totalResults == 0) {
                        sendNotification(id, context.getString(R.string.news_update))
                    } else {
                        sendNotification(id, response.articles.random())
                    }
                }
            })
    }

    private fun sendNotification(id: Int, message: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(NOTIFICATION_ID, id)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent: PendingIntent = getActivity(
            context,
            0,
            intent, if (SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(message)
            .setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }


    private fun sendNotification(id: Int, news: Article) {
        val intent = Intent(context, WebView::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }.putExtra("url", news.url).putExtra("toMain", true)

        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)

        Picasso.get().load(news.urlToImage).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                val bitmap = AppCompatResources.getDrawable(context, R.drawable.ic_logo)?.toBitmap()
                notify(notification, intent, bitmap!!, news, notificationManager, id)
            }

            override fun onBitmapLoaded(bitmapImage: Bitmap, from: Picasso.LoadedFrom?) {
                notify(notification, intent, bitmapImage, news, notificationManager, id)
            }
        })

    }

    private fun notify(
        notification: NotificationCompat.Builder,
        intent: Intent,
        bitmapImage: Bitmap,
        news: Article,
        notificationManager: NotificationManager,
        id: Int,
    ) {
        val pendingIntent: PendingIntent =
            getActivity(
                context,
                0,
                intent, if (SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        notification
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmapImage))
            .setLargeIcon(bitmapImage)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(news.title)
            .setContentText(news.description)
            .setDefaults(DEFAULT_ALL).setContentIntent(pendingIntent).setAutoCancel(true)

        notification.priority = PRIORITY_MAX

        if (SDK_INT >= O) {
            notification.setChannelId(NOTIFICATION_CHANNEL)

            val ringtoneManager = getDefaultUri(TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                .setContentType(CONTENT_TYPE_SONIFICATION).build()

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, IMPORTANCE_HIGH)

            channel.enableLights(true)
            channel.lightColor = RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(id, notification.build())
    }


    companion object {
        const val NOTIFICATION_ID = "swen_notification_id"
        const val NOTIFICATION_NAME = "Swen"
        const val NOTIFICATION_CHANNEL = "Swen_channel_01"
        const val NOTIFICATION_WORK = "Swen_notification_work"
    }
}