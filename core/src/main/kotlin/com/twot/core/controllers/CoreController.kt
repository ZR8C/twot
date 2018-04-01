package com.twot.core.controllers

import com.twot.core.models.Tweet
import com.twot.core.models.TweetSource
import com.twot.core.providers.JsoupProvider
import com.twot.core.providers.TweetProvider
import com.twot.core.providers.TwitRssProvider
import io.requery.Persistable
import io.requery.kotlin.EntityStore
import io.requery.kotlin.asc
import io.requery.kotlin.desc
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import java.time.LocalDateTime
import java.util.*
import java.util.logging.Logger

class CoreController(private val data: KotlinReactiveEntityStore<Persistable>,
                     private val providers: List<TweetProvider> = listOf(JsoupProvider())) {

    companion object {
        val log = Logger.getLogger(this::class.java.simpleName)
    }

    private val providerHistory = mapOf<TweetSource, TweetProvider>()

    fun getAccounts(): MutableList<TweetSource> = data.select(TweetSource::class).orderBy(TweetSource::lastUpdated.asc()).get().toList()

    fun getEnabledLastUpdatedSource(): TweetSource? = data.select(TweetSource::class).where(TweetSource::enabled eq true).orderBy(TweetSource::lastUpdated.asc()).limit(1).get().firstOrNull()

    fun getTweets(): MutableList<Tweet> = data.select(Tweet::class).join(TweetSource::class).on(Tweet::tweetSource eq TweetSource::title).where(TweetSource::enabled eq true).orderBy(Tweet::pubDate.desc()).get().toList()

    fun getLatestTweets(source: TweetSource): List<Tweet> {
        val latestTweets = providers[0].get(source)    //todo alternate providers

        source.lastUpdated = Date()
        data.update(source)

        val newTweets = mutableListOf<Tweet>()

        data.withTransaction {
            latestTweets.forEach {
                if (!tweetExists(it)) {        //todo is this slow?
                    log.fine("Found new tweet ${it.link}")
                    println("Found new tweet ${it.link}")
                    insert(it)
                    newTweets.add(it)
                }
            }
            update(source)
        }.blockingGet()

        log.fine("Found ${newTweets.size} new tweets for source ${source.title}")

        return newTweets
    }

    private fun tweetExists(tweet: Tweet) = data.count(Tweet::class).where(Tweet::link eq tweet.link).limit(1).get().value() != 0
}

