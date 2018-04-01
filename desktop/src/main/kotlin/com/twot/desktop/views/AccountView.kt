package com.twot.desktop.views

import com.twot.core.models.TweetSource
import com.twot.core.models.TweetSourceType
import com.twot.core.models.TweetSourceType.*
import com.twot.desktop.controllers.TwotController
import javafx.geometry.Pos
import tornadofx.*

class AccountView : View() {

    val tweetController: TwotController by inject()

    override val root = listview (tweetController.accounts) {
        maxWidth = 200.0
        cellFragment(AccountCellFragment::class)
    }
}

class AccountModel : ItemViewModel<TweetSource>() {

    val enabled = bind(TweetSource::enabled)

    val titleFormatted = bind {
        var titleFormatted = "@" + item?.title

        if (item?.type == SEARCH_TERM)
            titleFormatted =  "#" + item?.title

        titleFormatted.toProperty()
    }
}

class AccountCellFragment : ListCellFragment<TweetSource>() {
    val tweetSource = AccountModel().bindTo(this)
    val tweetController: TwotController by inject()

    override val root = hbox {
        checkbox (property = tweetSource.enabled) {
            setOnMouseClicked {
                runAsync {
                    log.fine { "AccountCellFragment enabled changed $isSelected" }
                    tweetSource.item.enabled = isSelected
                    TwotController.data.update(tweetSource.item).blockingGet()
                }
            }
        }

        label (tweetSource.titleFormatted) {
            alignment = Pos.CENTER
            style {
                alignment = Pos.CENTER
                fontSize = 10.px
            }
        }

        contextmenu {
            item("Update now").action {
                runAsync {
                    tweetController.fetchNewTweets(tweetSource.item)
                }
            }
            item("Remove").action {
                tweetController.deleteSource(tweetSource.item)
            }
        }
    }
}