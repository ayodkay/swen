package com.ayodkay.apps.swen.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ayodkay.apps.swen.R
import com.ayodkay.apps.swen.helper.adapter.AdsRecyclerView
import com.ayodkay.apps.swen.helper.room.country.CountryDatabase
import com.ayodkay.apps.swen.helper.room.userlocation.LocationDatabase
import com.ayodkay.apps.swen.model.News
import com.ayodkay.apps.swen.model.NewsArticle
import com.ayodkay.apps.swen.view.AskLocation
import com.ayodkay.apps.swen.viewmodel.NewViewModel
import org.json.JSONObject
import java.util.*

object Helper{

    fun getCountryDatabase(context: Context): CountryDatabase {
        return Room.databaseBuilder(
            context,
            CountryDatabase::class.java, "country"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun getLocationDatabase(context: Context): LocationDatabase {
        return Room.databaseBuilder(
            context,
            LocationDatabase::class.java, "location"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }

    fun available(country:String):Boolean{
        val ac  = arrayListOf(
            "ae","ar","at","au","be","bg","br","ca","ch","cn","co","cu","cz","de","eg","fr","gb"
            ,"gr","hk","hu","id","ie","il","in","it","jp","kr","lt","lv","ma","mx","my","ng",
            "nl","no","nz","ph","pl","pt","ro","rs","ru","sa","se","sg","si","sk","th","tr","tw"
            ,"ua","us","ve","za"
        )
        if (ac.contains(country.toLowerCase(Locale.ROOT))){
            return true
        }
        return false
    }


    fun top5Country(country: String): Boolean {
        val ac = arrayListOf(
            "br", "in", "ar", "us", "ng", "de", "fr"
        )
        if (ac.contains(country.toLowerCase(Locale.ROOT))) {
            return true
        }
        return false
    }

    @JvmStatic
    @Deprecated("changed to retrofit")
    fun handleJson(response: JSONObject): ArrayList<News> {

        val news: ArrayList<News> = arrayListOf()

        val result = response.getJSONArray("articles")

        for (results_loop in 0 until result.length()){
            val results = result.getJSONObject(results_loop)

            val source = results.getJSONObject("source")
            news.add(
                News(
                    source.getString("name"),
                    results.getString("author"),
                    results.getString("title"),
                    results.getString("description"),
                    results.getString("url"),
                    results.getString("urlToImage"),
                    results.getString("publishedAt"),
                    results.getString("content"),
                )
            )
        }

        return news
    }


    @JvmStatic
    fun setupFragment(
        category: String, frag: Fragment, inflater: LayoutInflater,
        container: ViewGroup?, q: String? = ""
    ): View? {


        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        setUpObserver(category, q, frag, root)

        refresh.setOnRefreshListener {
            setUpObserver(category, q, frag, root)
        }

        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserver(category: String, q: String?, frag: Fragment, root: View) {

        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)
        val articleArrayList = arrayListOf<NewsArticle>()

        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyText = root.findViewById<TextView>(R.id.emptyText)

        val db = getCountryDatabase(frag.requireContext())

        var country = ""
        try {
            country = db.countryDao().getAll().country
        } catch (e: Exception) {
            frag.requireContext()
                .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
            frag.requireActivity().finish()
        }

        with(newViewModel) {
            try {
                country = db.countryDao().getAll().country
            } catch (e: Exception) {
                frag.requireContext()
                    .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
                frag.requireActivity().finish()
            }

            getHeadlineFromRepo(country = country, q = q, category = category, pageSize = 100)
                .observe(frag, { newsResponse ->

                    if (newsResponse != null) {
                        if (newsResponse.totalResults == 0) {
                            root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                            if (newsResponse.status == "400") {
                                emptyText.text = "Internet Error"
                            }
                            emptyText.visibility = View.VISIBLE
                            root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility =
                                View.GONE
                            refresh.isRefreshing = false
                        } else {
                            root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility =
                                View.VISIBLE
                            root.findViewById<ImageView>(R.id.empty).visibility = View.GONE
                            emptyText.visibility = View.GONE
                            root.findViewById<TextView>(R.id.totalResults).text =
                                "${newsResponse.totalResults} ${
                                    frag.resources.getString(
                                        R.string.articles_found
                                    )
                                }"
                            root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                                layoutManager = LinearLayoutManager(frag.context)
                                hasFixedSize()
                                articleArrayList.addAll(newsResponse.articles)
                                adapter =
                                    AdsRecyclerView(articleArrayList, frag, frag.requireContext())
                            }
                            refresh.isRefreshing = false
                        }
                    }

                })
        }
    }


    @JvmStatic
    fun setupEveryThingFragment(
        frag: Fragment, inflater: LayoutInflater,
        container: ViewGroup?, q: String? = ""
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        setUpObserverEveryTime(q, root, frag)

        refresh.setOnRefreshListener {
            setUpObserverEveryTime(q, root, frag)
        }
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun setUpObserverEveryTime(q: String?, root: View, frag: Fragment) {

        val articleArrayList = arrayListOf<NewsArticle>()
        val newViewModel = ViewModelProvider(frag).get(NewViewModel::class.java)
        val refresh = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyText = root.findViewById<TextView>(R.id.emptyText)

        val db = getCountryDatabase(frag.requireContext())
        var language = ""
        try {
            language = db.countryDao().getAll().iso
        } catch (e: Exception) {
            frag.requireContext()
                .startActivity(Intent(frag.requireContext(), AskLocation::class.java))
            frag.requireActivity().finish()
        }
        newViewModel.getEveryThingFromRepo(
            q = q,
            sort_by = "newest",
            language = language,
            pageSize = 100
        )
            .observe(frag, { newsResponse ->
                if (newsResponse.totalResults == 0) {
                    root.findViewById<ImageView>(R.id.empty).visibility = View.VISIBLE
                    emptyText.visibility = View.VISIBLE
                    if (newsResponse.status == "400") {
                        emptyText.text = "Internet Error"
                    }
                    root.findViewById<RecyclerView>(R.id.newsRecyclerView).visibility = View.GONE
                } else {
                    root.findViewById<TextView>(R.id.totalResults).text =
                        "${newsResponse.totalResults} ${frag.resources.getString(R.string.articles_found)}"

                    root.findViewById<RecyclerView>(R.id.newsRecyclerView).apply {
                        layoutManager = LinearLayoutManager(frag.context)
                        articleArrayList.addAll(newsResponse.articles)
                        hasFixedSize()
                        adapter = AdsRecyclerView(articleArrayList, frag, frag.requireContext())
                    }
                }

            })
    }
}