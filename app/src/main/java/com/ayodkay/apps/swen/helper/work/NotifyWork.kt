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
import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
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

        val pendingIntent = getActivity(applicationContext, 0, intent, 0)
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
        val bigIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)
        val bitmap = try {
            Picasso.get().load(news.urlToImage).get()
        } catch (e: Exception) {
            bigIcon
        }
        val pendingIntent: PendingIntent = getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_logo)
            .setLargeIcon(bigIcon)
            .setContentTitle(news.title)
            .setContentText(news.description)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
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