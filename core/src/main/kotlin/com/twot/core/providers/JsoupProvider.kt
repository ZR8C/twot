package com.twot.core.providers

import com.twot.core.models.Tweet
import com.twot.core.models.TweetEntity
import com.twot.core.models.TweetSource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class JsoupProvider : TweetProvider() {
    override fun getTweets(source: TweetSource): List<Tweet> {
        val document = Jsoup.connect("https://m.twitter.com/${source.title}")
                .userAgent("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3B48b Safari/419.3")
                .get()

        if (source.imageUrl.isNullOrBlank()) {
            val avatar = document.select("td.avatar")
            val img = avatar.select("img")
            val imgUrl = img.attr("src")

            if (!imgUrl.isNullOrBlank())
                source.imageUrl = imgUrl
        }

        return parseMobileResponse(source, document)
    }

    override fun search(source: TweetSource): List<Tweet> {
        val document = Jsoup.connect("https://m.twitter.com/search?q=${source.title}")
                .userAgent("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420.1 (KHTML, like Gecko) Version/3.0 Mobile/3B48b Safari/419.3")
                .get()

        return parseMobileResponse(source, document)
    }

    private fun parseMobileResponse(source: TweetSource, document: Document) = document.select("table.tweet").map{
        item ->

        TweetEntity().apply {
            viewed = false

            val snowflake = item.select("div.tweet-text").attr("data-id")
            pubDate = snowflake.snowFlakeDate()
            content = item.select("div.dir-ltr").text()
            creator = item.select("div.username").text()
            tweetSource = source
            link = "https://twitter.com${item.attr("href")}"
            imageUrl = item.select("td.avatar").select("img").attr("src")
        }
    }

}

const val TWITTER_EPOCH = 1288834974657

/**
 * Parses Date from a twitter snowflake Id
 * Shifting right 22 bits then adding to the twitter epoch constant gives us
 * the time in millis since epoch (1970-01-01T00:00:00Z)
 */
fun String.snowFlakeDate() : Date = toLong().snowFlakeDate()

/**
 * Parses Date from a twitter snowflake Id
 * Shifting right 22 bits then adding to the twitter epoch constant gives us
 * the time in millis since epoch (1970-01-01T00:00:00Z)
 */

fun Long.snowFlakeDate() : Date = Date(shr(22) + TWITTER_EPOCH)