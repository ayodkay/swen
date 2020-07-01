package com.ayodkay.apps.swen.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayodkay.apps.swen.helper.App
import com.kymjs.rxvolley.RxVolley
import com.kymjs.rxvolley.client.HttpCallback
import com.kymjs.rxvolley.http.VolleyError
import org.json.JSONObject

class NewsViewModel : ViewModel(){


    private var _news: MutableLiveData<JSONObject>? = null


    // get all the loaded users
    fun getNews(url:String):MutableLiveData<JSONObject> {
        if(_news ==null){
            _news = MutableLiveData()
            loadNews(url)
        }

        Log.d("TAG", "getNews: $url")
        return _news!!
    }

    //load all users Asynchronously
    private fun loadNews(url: String){
        val callback = object : HttpCallback(){
            override fun onSuccess(response: String?) {
                val jsonObject = JSONObject(response!!)

                _news?.value = jsonObject
            }

            override fun onFailure(error: VolleyError?) {
                super.onFailure(error)

                Toast.makeText(App.context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        RxVolley.Builder()
            .url(url)
            .httpMethod(RxVolley.Method.GET) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
            .cacheTime(6) //default: get 5min, post 0min
            .contentType(RxVolley.ContentType.JSON) //default FORM or JSON
            .shouldCache(true) //default: get true, post false
            .callback(callback)
            .encoding("UTF-8") //default
            .doTask()
    }
}