package com.twot.desktop.views

import com.twot.desktop.controllers.TwotController
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import tornadofx.*

class AddUserView : View("Add user") {
    private val maxUserLength = 21

    val tweetController: TwotController by inject()

    var textField: TextField by singleAssign()

    override val root = vbox {
        hbox {
            label("Username: ")
            textField = textfield("kotlin")

            textField.lengthProperty().onChange {
                if (it > maxUserLength) {
                    runLater {
                        textField.text = textField.text.take(maxUserLength)
                    }
                }
            }
        }
        button("Add").action {
            if (!textField.text.isNullOrBlank()) {
                if(tweetController.addSource(textField.text))
                    close()
                else
                    alert(Alert.AlertType.WARNING, "Duplicate entry", "User already exists")
            }
        }
    }
}