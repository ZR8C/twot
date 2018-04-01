package com.twot.core.models

import io.requery.*
import java.time.LocalDateTime
import java.util.*

@Entity
interface TweetSource : Persistable {

    //todo make title + type key
    //i.e. twitter account handle or search term
    @get:Key
    var title: String

    // account or searchTerm
    var type: TweetSourceType

    //profile image of account
    var imageUrl: String?

    //whether to show information about this source
    var enabled: Boolean

    @get:OneToMany(mappedBy = "tweetSource", cascade = arrayOf(CascadeAction.NONE))
    var tweets: List<Tweet>

    var lastUpdated: Date
}

enum class TweetSourceType {
    ACCOUNT,
    SEARCH_TERM
}