package com.ayodkay.apps.swen.helper.work

import android.content.Context
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.onesignal.Notification
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotificationSender
import com.github.ayodkay.builder.TopHeadlinesBuilder
import com.github.ayodkay.client.NewsApiClient
import com.github.ayodkay.interfaces.ArticlesResponseCallback
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.ArticleResponse

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

        val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()

        newsApiClientWithObserver.getTopHeadlines(
            topHeadlinesBuilder,
            object : ArticlesResponseCallback {
                override fun onFailure(throwable: Throwable) {
                    sendNotification(context.getString(R.string.news_update))
                }

                override fun onSuccess(response: ArticleResponse) {
                    if (response.totalResults == 0) {
                        sendNotification(context.getString(R.string.news_update))
                    } else {
                        sendNotification(response.articles.random())
                    }
                }
            }
        )
    }

    private fun sendNotification(message: String) {
        OneSignalNotificationSender
            .sendDeviceNotification(
                Notification(
                    "Breaking News",
                    setNotificationData(message),
                    "ic_stat_onesignal_default.png",
                    context.getString(R.string.notification_icon),
                    "[]", true, 0
                ),
                context
            )
    }

    private fun setNotificationData(message: String): Array<Array<String>> {
        return arrayOf(
            arrayOf(
                message, "\u200E", context.getString(R.string.ic_logo), "", ""
            )
        )
    }

    private fun sendNotification(news: Article) {
        OneSignalNotificationSender
            .sendDeviceNotification(
                Notification(
                    "Breaking News",
                    setNotificationData(news),
                    "ic_stat_onesignal_default.png",
                    context.getString(R.string.notification_icon),
                    "[]", true, 0
                ),
                context
            )
    }

    private fun setNotificationData(news: Article): Array<Array<String>> {
        return arrayOf(
            arrayOf(news.title, news.description, news.urlToImage, news.urlToImage, news.url)
        )
    }

    companion object {
        const val NOTIFICATION_ID = "swen_notification_id"
        const val NOTIFICATION_WORK = "Swen_notification_work"
    }
}
