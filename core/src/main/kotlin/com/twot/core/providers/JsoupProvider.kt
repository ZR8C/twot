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

    private fun parseMobileResponse(source: TweetSource, document: Document): List<TweetEntity> {
        // select all tweets - these can potentially be linked tweets, and so not always from the original source we wanted
        return document.select("table.tweet").map{ tweet ->

            //create the tweet entity and set metadata
            TweetEntity().apply {
                viewed = false
                tweetSource = source

                //grab the snowflake ID
                val snowflake = tweet.select("div.tweet-text").attr("data-id")

                //parse the date from snowflake ID
                pubDate = snowflake.snowFlakeDate()

                //grab the creator, note this also usually includes info on replying
                creator = tweet.select("div.username").text()

                //get main tweet text
                content = tweet.select("div.dir-ltr").text()

                //grab the link to the tweet
                link = "https://twitter.com${tweet.attr("href")}"

                //grab the image url, usually profile image of the tweet source
                imageUrl = tweet.select("td.avatar").select("img").attr("src")
            }
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