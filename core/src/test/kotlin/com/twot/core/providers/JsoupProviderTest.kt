package com.twot.core.providers

import com.twot.core.models.TweetSourceType
import org.apache.commons.lang3.time.FastDateFormat
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class JsoupProviderTest {

    val jsoupProvider = JsoupProvider()

    @Test
    fun getTweets() {
        val tweets = jsoupProvider.getTweets("hhariri")

        assertTrue(tweets.isNotEmpty())

        val tweet = tweets.first()

        with (tweet) {
            assertTrue(content.isNotBlank())
            assertFalse(viewed)
            assertTrue(creator.isNotBlank())
            assertTrue(tweetSource.title == "hhariri")
            assertTrue(tweetSource.type == TweetSourceType.ACCOUNT)
            assertTrue(link.startsWith("https://twitter.com"))
        }
    }

    @Test
    fun search() {
        val tweets = jsoupProvider.search("london")

        assertTrue(tweets.isNotEmpty())

        val tweet = tweets.first()

        with (tweet) {
            assertTrue(content.isNotBlank())
            assertFalse(viewed)
            assertTrue(creator.isNotBlank())
            assertTrue(tweetSource.title == "london")
            assertTrue(tweetSource.type == TweetSourceType.SEARCH_TERM)
            assertTrue(link.startsWith("https://twitter.com"))
        }
    }

}