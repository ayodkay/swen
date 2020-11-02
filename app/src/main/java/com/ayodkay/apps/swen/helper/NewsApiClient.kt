package com.ayodkay.apps.swen.helper


@Deprecated("switched to retrofit", ReplaceWith("retrofit", ))
object NewsApiClient {

    private fun apiKey(): String {
        return ""
    }
    @JvmStatic
    @JvmOverloads
    fun getSources(
        category:String?="", language:String?="", country:String?=""): String{

        return "https://newsapi.org/v2/sources?category=${category}&language=${language}&country=${country}&apiKey=${apiKey()}"
    }

    @JvmStatic
    @JvmOverloads
    fun getTopHeadline(
        q:String?="", sources:String?="", category:String?="", language:String?="",
        country:String?="",  pageSize:Int): String{

        return "https://newsapi.org/v2/top-headlines?q=${q}&sources=${sources}&category=${category}&pageSize=${pageSize}&language=${language}&country=${country}&apiKey=${apiKey()}"
    }

    @JvmStatic
    @JvmOverloads
    fun getEverything(
        q:String?="", sources:String?="", domains:String?="",qInTitle:String?="",from_param:String?="",
        to:String?="", language:String?="", sort_by:String?="", pageSize:Int): String {

        return "https://newsapi.org/v2/everything?q=${q}&sources=${sources}&domains=${domains}&qInTitle=${qInTitle}&from_param=${from_param}&to=${to}&language=${language}&sortBy=${sort_by}&pageSize=${pageSize}&apiKey=${apiKey()}"
    }
}