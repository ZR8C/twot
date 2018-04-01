package com.twot.core.providers

import com.github.kittinunf.fuel.httpGet
import com.twot.core.models.*

class TwitRssProvider : TweetProvider() {

    override fun getTweets(source: TweetSource): List<Tweet> {
        val (_, b, result) = "https://twitrss.me/twitter_user_to_rss/?user=${source.title}".httpGet().responseObject(Rss.Deserializer())

        //todo handle non 200 response
        val rss = result.get()
        val channel = rss.channel

        //set profile imageUrl if one isn't set yet
        if (source.imageUrl.isNullOrBlank())
            source.imageUrl = channel.image?.url

        if (channel.item == null) return emptyList()

        return itemsToTweets(source, channel.item)
    }

    override fun search(source: TweetSource): List<Tweet> {
        val (_, _, result) = "https://twitrss.me/twitter_search_to_rss/?term=${source.title}".httpGet().responseObject(Rss.Deserializer())

        //todo handle non 200 response
        val rss = result.get()
        val channel = rss.channel

        if (channel.item == null) return emptyList()

        return itemsToTweets(source, channel.item)
    }

    private fun itemsToTweets(source: TweetSource, items: List<Item>) = items.map {
        item ->
        val tweet = TweetEntity()

        with (tweet) {
            viewed = false
            link = item.link
            pubDate = item.pubDate
            content = item.title
            creator = item.creator
            tweetSource = source
            imageUrl = source.imageUrl
        }

        tweet
    }
}