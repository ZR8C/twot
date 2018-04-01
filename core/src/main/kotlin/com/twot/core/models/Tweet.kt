package com.twot.core.models

import io.requery.*
import javafx.beans.property.SimpleStringProperty
import java.util.*

@Entity
interface Tweet : Persistable {

    @get:Key
    var link: String

    var pubDate: Date

    //main body of the tweet
    var content: String

    var viewed: Boolean

    var creator: String

    //todo fix when polymorphism works with requery, having account or searchTerm with common superclass
    @get:ManyToOne(cascade = arrayOf(CascadeAction.NONE))
    var tweetSource: TweetSource

    var imageUrl: String

    @Transient
    fun isRetweet() : Boolean {
        if (tweetSource.type != TweetSourceType.ACCOUNT) return false

        return creator.contains("@${tweetSource.title}", ignoreCase = true)
    }
}