package com.github.haseoo.cpuschedsim

import com.github.haseoo.cpuschedsim.controller.MainWindowController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL
import java.util.*


class Main : Application() {
    override fun start(stage: Stage) {
        val mainWindow = FXMLLoader(getResourceURL("main.fxml"))
        mainWindow.setController(MainWindowController())
        val root = mainWindow.load<Parent>()
        stage.title = "CPU scheduler simulator"
        stage.scene = Scene(root)
        stage.isResizable = false
        stage.show()
    }

    fun main(args: Array<String>) {
        launch(*args)
    }

}

fun main() {
    Application.launch(Main::class.java)
}

fun getResourceURL(relativePath: String): URL {
    return Objects.requireNonNull(Main::class.java.classLoader.getResource(relativePath))
}