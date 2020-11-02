package com.ayodkay.apps.swen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ayodkay.apps.swen.model.NewsResponse
import com.ayodkay.apps.swen.networking.NewsRepository

class NewViewModel : ViewModel() {
    private val newsRepository = NewsRepository.instance

    private fun apiKey(): String {
        return "dc0576cde63048f090c121ca1615e03f"
    }


    fun getHeadlineFromRepo(
        q: String? = "", sources: String? = "", category: String? = "", language: String? = "",
        country: String? = "", pageSize: Int
    ): LiveData<NewsResponse> {

        return newsRepository
            .getHeadlines(
                key = apiKey(), country = country, q = q,
                category = category, pageSize = pageSize, sources = sources, language = language
            )
    }

    fun getEveryThingFromRepo(
        q: String? = "", sources: String? = "", domains: String? = "", qInTitle: String? = "",
        from_param: String? = "", to: String? = "", language: String? = "", sort_by: String? = "",
        pageSize: Int
    ): LiveData<NewsResponse> {

        return newsRepository.getEveryThing(
                key = apiKey(),
                domains = domains,
                q = q,
                qInTitle = qInTitle,
                from_param = from_param,
                pageSize = pageSize,
                sources = sources,
                language = language,
                sort_by = sort_by,
                to = to
            )
    }
}