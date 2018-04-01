package com.twot.desktop.views

import com.twot.core.models.Tweet
import com.twot.core.models.TweetSourceType
import com.twot.desktop.controllers.TwotController
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TweetView : View() {

    val tweetController: TwotController by inject()

    override val root = listview(tweetController.tweets) {
        vgrow = Priority.ALWAYS
        hgrow = Priority.ALWAYS

        cellFragment(TweetCellFragment::class)
    }
}

class TweetModel : ItemViewModel<Tweet>() {
    //todo move functions into Tweet?
    val tweetController: TwotController by inject()

    val pubDateFormatted = bind {
        item?.pubDate?.toFormattedString().toProperty()
    }

    val title = bind(Tweet::content)

    val viewed = bind(Tweet::viewed)

    val link = bind(Tweet::link)

    val sourceTitle = bind {
        var typeIcon = "@"

        if (item?.tweetSource?.type == TweetSourceType.SEARCH_TERM)
            typeIcon = "#"

        val title = item?.tweetSource?.title

        (typeIcon + title).toProperty()
    }
     val creator = bind(Tweet::creator)

    val image = bind { tweetController.getImage(item?.imageUrl).toProperty() }
}

fun Date.toFormattedString() : String {
    val format = SimpleDateFormat("h:mm a - d MMM yyyy")
    return format.format(this)
}

class TweetCellFragment : ListCellFragment<Tweet>() {
    val tweet = TweetModel().bindTo(this)

    override val root = hbox {
        imageview (tweet.image) {
            fitHeight = 48.0
            isPreserveRatio = true
        }
        vbox {
            hbox {
                text("*") {
                    fill = Color.RED
                    style {
                        fontSize = 20.px
                    }

                    removeWhen(tweet.viewed)
                }

                text (tweet.sourceTitle) {
                    fill = Color.BLUE
                }
                text(tweet.creator) {
                    fill = Color.CORNFLOWERBLUE
                }

            }
            text(tweet.pubDateFormatted)

            text (tweet.title){
                style {
                    wrapText = true
                }
                wrappingWidthProperty().bind(primaryStage.widthProperty() - 300)
            }

            onDoubleClick {
                hostServices.showDocument(tweet.link.value)
            }
        }
    }

}