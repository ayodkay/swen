package com.ayodkay.apps.swen.helper.deprecated


@Deprecated("switched to retrofit", ReplaceWith("retrofit",))
class NewsApiClient(private val apiKey: String) {
    fun getSources(
        category: String? = "", language: String? = "", country: String? = ""
    ): String {

        return "https://newsapi.org/v2/sources?category=${category}&language=${language}&country=${country}&apiKey=${apiKey}"
    }

    fun getTopHeadline(
        q: String? = "", sources: String? = "", category: String? = "", language: String? = "",
        country: String? = "", pageSize: Int
    ): String {

        return "https://newsapi.org/v2/top-headlines?q=${q}&sources=${sources}&category=${category}&pageSize=${pageSize}&language=${language}&country=${country}&apiKey=${apiKey}"
    }

    fun getEverything(
        q: String? = "",
        sources: String? = "",
        domains: String? = "",
        qInTitle: String? = "",
        from_param: String? = "",
        to: String? = "",
        language: String? = "",
        sort_by: String? = "",
        pageSize: Int
    ): String {

        return "https://newsapi.org/v2/everything?q=${q}&sources=${sources}&domains=${domains}&qInTitle=${qInTitle}&from_param=${from_param}&to=${to}&language=${language}&sortBy=${sort_by}&pageSize=${pageSize}&apiKey=${apiKey}"
    }
}