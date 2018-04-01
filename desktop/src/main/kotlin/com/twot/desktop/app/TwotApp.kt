package com.twot.desktop.app

import com.twot.desktop.controllers.TwotController
import com.twot.desktop.views.TwotMainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*

class TwotApp : App(TwotMainView::class) {
    val tweetController: TwotController by inject()

    override fun start(stage: Stage) {
        super.start(stage)

        val icon = Image(this::class.java.getResourceAsStream("/appicon.png"))
        addStageIcon(icon)

        stage.focusedProperty().onChange {
            if (!it) tweetController.setAllTweetsRead()
        }
    }
}