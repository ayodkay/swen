package com.ayodkay.apps.swen.networking

import androidx.lifecycle.MutableLiveData
import com.ayodkay.apps.swen.helper.AppLog
import com.ayodkay.apps.swen.model.NewsResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsRepository {
    private val newsApi: NewsApi = RetrofitService.createService(NewsApi::class.java)

    fun getHeadlines( q:String?="", sources:String?="", category:String?="", language:String?="",
                 country:String?="",  pageSize:Int,key:String?): MutableLiveData<NewsResponse> {
        val newsData = MutableLiveData<NewsResponse>()

        newsApi.getHeadlines(apiKey = key,country = country, q = q, category = category,
            pageSize = pageSize, newsSource = sources,language = language).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>,
                                    response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    newsData.value = response.body()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                newsData.value = NewsResponse("400",0, emptyList())
            }
        })
        return newsData
    }

    fun getEveryThing(q:String?="", sources:String?="", domains:String?="",qInTitle:String?="",
                       from_param:String?="", to:String?="", language:String?="", sort_by:String?="",
                       pageSize:Int,key:String?): MutableLiveData<NewsResponse> {
        val newsData = MutableLiveData<NewsResponse>()

        newsApi.getEveryThing(apiKey = key,domains = domains, q = q, qInTitle = qInTitle,
            from_param = from_param, pageSize = pageSize, newsSource = sources,language = language,
            sort_by = sort_by, to = to).enqueue(object : Callback<NewsResponse> {

            override fun onResponse(call: Call<NewsResponse>,
                                    response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    newsData.value = response.body()
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                newsData.value = NewsResponse("400",0, emptyList())
            }
        })
        return newsData
    }

    companion object {
        @Volatile
        private var newsRepository: NewsRepository? = null
        val instance: NewsRepository
            get() {
                if (newsRepository == null) {
                    newsRepository = NewsRepository()
                }
                return newsRepository!!
            }
    }
}


