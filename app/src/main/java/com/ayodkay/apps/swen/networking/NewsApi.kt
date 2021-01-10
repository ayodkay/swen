package com.ayodkay.apps.swen.networking

import com.ayodkay.apps.swen.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    fun getHeadlines(
        @Query("apiKey") apiKey: String?,
        @Query("sources") newsSource: String? = "",
        @Query("q") q: String? = "",
        @Query("category") category: String? = "",
        @Query("language") language: String? = "",
        @Query("pageSize") pageSize: Int? = 0,
        @Query("country") country: String? = ""
    ): Call<NewsResponse>

    @GET("everything")
    fun getEveryThing(
        @Query("apiKey") apiKey: String?,
        @Query("q") q: String? = "",
        @Query("sources") newsSource: String? = "",
        @Query("domains") domains:String?="",
        @Query("qInTitle") qInTitle:String?="",
        @Query("from_param") from_param:String?="",
        @Query("to") to:String?="",
        @Query("language") language: String? = "",
        @Query("sort_by") sort_by:String?="",
        @Query("pageSize") pageSize: Int? = 0,
    ): Call<NewsResponse>
}