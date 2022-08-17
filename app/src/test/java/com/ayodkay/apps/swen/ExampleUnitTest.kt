package com.ayodkay.apps.swen

import com.google.gson.Gson
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

val USER_AGENT: String =
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36" // ktlint-disable max-line-length

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSoup() {
        // Fetch the page
        // Fetch the page
        val doc = Jsoup.connect("https://google.com/search?q=test").userAgent(USER_AGENT).get()
        // Traverse the results
        // Traverse the results
        for (result in doc.select("h3.r a")) {
            val title = result.text()
            val url = result.attr("href")
            // Now do something with the results (maybe something more useful than just printing to console)
            println("$title -> $url")
        }
    }

    @Test
    fun tessttt() {
        val basic = "KEY"
        try {
            val jsonResponse: String
            val url = URL("https://onesignal.com/api/v1/notifications")
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.useCaches = false
            con.doOutput = true
            con.doInput = true
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            con.setRequestProperty(
                "Authorization", "Basic $basic"
            )
            val map: MutableMap<String?, Any?> = HashMap()
            val filter = arrayListOf(
                mapOf(
                    Pair("field", "tag"), Pair("key", "country"),
                    Pair("relation", "="), Pair("value", "br")
                )
            )
            val content = mapOf(
                Pair("en", "English language message")
            )
            map["filters"] = filter
            map["app_id"] = "1b294a36-a306-4117-8d5e-393ee674419d"
            map["contents"] = content

            con.requestMethod = "POST"
            val strJsonBody = Gson().toJson(map)
            val sendBytes = strJsonBody.toByteArray(charset("UTF-8"))
            con.setFixedLengthStreamingMode(sendBytes.size)
            val outputStream: OutputStream = con.outputStream
            outputStream.write(sendBytes)
            val httpResponse: Int = con.responseCode
            if (httpResponse >= HttpURLConnection.HTTP_OK &&
                httpResponse < HttpURLConnection.HTTP_BAD_REQUEST
            ) {
                val scanner = Scanner(con.inputStream, "UTF-8")
                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                scanner.close()
            } else {
                val scanner = Scanner(con.errorStream, "UTF-8")
                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                scanner.close()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
