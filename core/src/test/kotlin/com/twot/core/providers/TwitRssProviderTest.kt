package com.twot.core.providers

import org.junit.Test

import org.junit.Assert.*

class TwitRssProviderTest {

    val twitRssProvider = TwitRssProvider()

    @Test
    fun getTweets() {
        val tweets = twitRssProvider.getTweets("hhariri")

        assertTrue(tweets.isNotEmpty())

        val tweet = tweets.first()

        with (tweet) {
            org.junit.Assert.assertTrue(content.isNotBlank())
            org.junit.Assert.assertFalse(viewed)
            org.junit.Assert.assertTrue(creator.isNotBlank())
            org.junit.Assert.assertTrue(tweetSource.title == "hhariri")
            org.junit.Assert.assertTrue(tweetSource.type == com.twot.core.models.TweetSourceType.ACCOUNT)
            org.junit.Assert.assertTrue(link.startsWith("https://twitter.com"))
        }
    }

    @Test
    fun search() {
        val tweets = twitRssProvider.search("london")

        assertTrue(tweets.isNotEmpty())

        val tweet = tweets.first()

        with (tweet) {
            org.junit.Assert.assertTrue(content.isNotBlank())
            org.junit.Assert.assertFalse(viewed)
            org.junit.Assert.assertTrue(creator.isNotBlank())
            org.junit.Assert.assertTrue(tweetSource.title == "london")
            org.junit.Assert.assertTrue(tweetSource.type == com.twot.core.models.TweetSourceType.SEARCH_TERM)
            org.junit.Assert.assertTrue(link.startsWith("https://twitter.com"))
        }
    }
}