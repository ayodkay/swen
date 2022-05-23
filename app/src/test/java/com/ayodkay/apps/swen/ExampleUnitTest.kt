package com.ayodkay.apps.swen

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

val USER_AGENT: String =
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36"

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
}
