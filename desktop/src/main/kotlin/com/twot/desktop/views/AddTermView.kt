package com.twot.desktop.views

import com.twot.core.models.TweetSourceType
import com.twot.core.models.TweetSourceType.*
import com.twot.desktop.controllers.TwotController
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import tornadofx.*

class AddTermView : View("Add term") {

    val tweetController: TwotController by inject()
    var textField: TextField by singleAssign()

    override val root = vbox {
        hbox {
            label("Term: ")
            textField = textfield("london")
        }
        button("Add").action {
            if (!textField.text.isNullOrBlank()) {
                if(tweetController.addSource(textField.text, SEARCH_TERM))
                    close()
                else
                    alert(Alert.AlertType.WARNING, "Duplicate entry", "Term already exists")
            }
        }
    }
}
