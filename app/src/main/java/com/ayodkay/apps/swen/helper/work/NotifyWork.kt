package com.ayodkay.apps.swen.helper.work

import android.content.Context
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.Helper
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotification
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotificationSender
import com.github.ayodkay.builder.TopHeadlinesBuilder
import com.github.ayodkay.client.NewsApiClient
import com.github.ayodkay.interfaces.ArticlesResponseCallback
import com.github.ayodkay.models.Article
import com.github.ayodkay.models.ArticleResponse
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotifyWork(private val context: Context, params: WorkerParameters) :
    Worker(context, params), KoinComponent {
    private val mixpanel: MixPanelInterface by inject()
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
            "general",
            "entertainment",
            "sports",
            "business",
            "health",
            "science",
            "technology"
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
                    val props = JSONObject()
                    props.put("source", "Notify Work")
                    props.put("reason", throwable.toString())
                    mixpanel.track("onFailure", props)
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
                OneSignalNotification(
                    "Breaking News",
                    message,
                    "\u200E",
                    "ic_stat_onesignal_default.png",
                    context.getString(R.string.notification_icon),
                    context.getString(R.string.ic_logo),
                    "",
                    "",
                    "[]",
                    true
                ),
                context
            )
    }

    private fun sendNotification(news: Article) {
        OneSignalNotificationSender
            .sendDeviceNotificationWithRequest(
                OneSignalNotification(
                    "Breaking News",
                    news.title,
                    news.description, "ic_stat_onesignal_default.png",
                    news.urlToImage, news.urlToImage, news.url, "", "[]",
                    true
                ),
                context
            )
    }

    companion object {
        const val NOTIFICATION_ID = "swen_notification_id"
        const val NOTIFICATION_WORK = "Swen_notification_work"
    }
}
