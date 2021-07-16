package com.ayodkay.apps.swen.helper.deprecated

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Deprecated("")
class NewsViewModel internal constructor(application: Application) : AndroidViewModel(application) {

    private var _news: MutableLiveData<JSONObject>? = null

    // get all the loaded users
    fun getNews(url: String): MutableLiveData<JSONObject> {
        if (_news == null) {
            _news = MutableLiveData()
            loadNews(url)
        }
        return _news!!
    }

    //load all users Asynchronously
    private fun loadNews(url: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Toast.makeText(
                            getApplication(),
                            "Unexpected code $response",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val jsonObject = JSONObject(response.body!!.string())
                        _news?.postValue(jsonObject)
                    }
                }
            }
        })
    }
}