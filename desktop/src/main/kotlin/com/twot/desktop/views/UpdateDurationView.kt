package com.twot.desktop.views

import com.twot.desktop.controllers.TwotController
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import tornadofx.*
import java.time.Duration

class UpdateDurationView : View("Set update duration") {

    val tweetController: TwotController by inject()

    var textField: TextField by singleAssign()

    override val root = vbox {
        hbox {
            label("Duration (seconds): ")

            textField = textfield(tweetController.updateDuration.get().seconds.toString())
        }
        button("Set").action {
            val text = textField.text
            if (text.isLong() && text.toLong() > 0) {
                tweetController.updateDuration.set(Duration.ofSeconds(text.toLong()))
                close()
            } else
                alert(Alert.AlertType.INFORMATION, "Invalid number", "Please enter a valid number > 0")
        }
    }
}

fun String.isLong() = toLongOrNull() != null
