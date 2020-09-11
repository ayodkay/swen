package com.ayodkay.apps.swen.helper

class NewsApiClient {

    private fun apiKey(): String {
        return "dc0576cde63048f090c121ca1615e03f"
    }

    companion object {
        @JvmStatic
        @JvmOverloads
        fun getSources(
            newsApiClient: NewsApiClient,
            category:String?="", language:String?="", country:String?=""): String{
    
            return "https://newsapi.org/v2/sources?category=${category}&language=${language}&country=${country}&apiKey=${newsApiClient.apiKey()}"
        }

        @JvmStatic
        @JvmOverloads
        fun getTopHeadline(
            newsApiClient: NewsApiClient,
            q:String?="", sources:String?="", category:String?="", language:String?="",
            country:String?="",  pageSize:Int): String{
    
            return "https://newsapi.org/v2/top-headlines?q=${q}&sources=${sources}&category=${category}&pageSize=${pageSize}&language=${language}&country=${country}&apiKey=${newsApiClient.apiKey()}"
        }

        @JvmStatic
        @JvmOverloads
        fun getEverything(
            newsApiClient: NewsApiClient,
            q:String?="", sources:String?="", domains:String?="",qInTitle:String?="",from_param:String?="",
            to:String?="", language:String?="", sort_by:String?="", pageSize:Int): String {
    
            return "https://newsapi.org/v2/everything?q=${q}&sources=${sources}&domains=${domains}&qInTitle=${qInTitle}&from_param=${from_param}&to=${to}&language=${language}&sortBy=${sort_by}&pageSize=${pageSize}&apiKey=${newsApiClient.apiKey()}"
        }
    }
}