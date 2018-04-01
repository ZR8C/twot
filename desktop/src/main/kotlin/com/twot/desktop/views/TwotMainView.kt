package com.twot.desktop.views

import com.twot.desktop.controllers.TwotController
import javafx.scene.layout.Priority
import tornadofx.*

class TwotMainView : View("Twot") {

    val tweetController: TwotController by inject()

    override val root = vbox {
        menubar {
            menu("Feeds") {
                item("Add user").action {
                    openInternalWindow(AddUserView::class)
                }
                item("Add term").action {
                    openInternalWindow(AddTermView::class)
                }
            }
            menu("Options") {
                item("Set all read").action {
                    tweetController.setAllTweetsRead()
                }
                item("Set update duration").action {
                    openInternalWindow(UpdateDurationView::class)
                }
            }
        }

        hbox {
            this += find(AccountView::class)
            this += find(TweetView::class)

            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
        }
    }
}





