package com.twot.core.providers

import com.twot.core.models.TweetSourceType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class JsoupProviderTest {

    val jsoupProvider = JsoupProvider()

    @Test
    fun getTweets() {
        val tweets = jsoupProvider.getTweets("hhariri")

        assertTrue(tweets.isNotEmpty())

        val tweet = tweets.first()

        with (tweet) {
            assertTrue(content.isNotBlank())
            assertTrue(pubDate != LocalDateTime.ofInstant(Instant.ofEpochMilli(TWITTER_EPOCH), ZoneOffset.UTC))
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
            assertTrue(pubDate != LocalDateTime.ofInstant(Instant.ofEpochMilli(TWITTER_EPOCH), ZoneOffset.UTC))
            assertFalse(viewed)
            assertTrue(creator.isNotBlank())
            assertTrue(tweetSource.title == "london")
            assertTrue(tweetSource.type == TweetSourceType.SEARCH_TERM)
            assertTrue(link.startsWith("https://twitter.com"))
        }
    }
}