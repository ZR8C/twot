package com.twot.core.providers

import com.twot.core.models.Tweet
import com.twot.core.models.TweetEntity
import com.twot.core.models.TweetSource
import com.twot.core.models.TweetSourceEntity
import com.twot.core.models.TweetSourceType.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class TweetProvider {

    fun get(source: TweetSource) = when(source.type) {
        ACCOUNT -> getTweets(source)
        SEARCH_TERM -> search(source)
    }

    fun getTweets(username: String): List<Tweet> {
        val user = TweetSourceEntity()
        user.title = username
        user.type = ACCOUNT
        user.enabled = true
        return getTweets(user)
    }

    abstract fun getTweets(source: TweetSource) : List<Tweet>

    fun search(term: String): List<Tweet> {
        val searchTerm = TweetSourceEntity()
        searchTerm.title = term
        searchTerm.type = SEARCH_TERM
        searchTerm.enabled = true
        return search(searchTerm)
    }

    abstract fun search(source: TweetSource) : List<Tweet>

}
