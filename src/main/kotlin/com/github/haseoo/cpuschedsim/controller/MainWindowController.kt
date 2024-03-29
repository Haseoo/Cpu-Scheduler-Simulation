package com.github.haseoo.cpuschedsim.controller

import com.github.haseoo.cpuschedsim.domain.CpuScheduler
import com.github.haseoo.cpuschedsim.generateProcesses
import com.github.haseoo.cpuschedsim.getResourceURL
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import javafx.util.Duration
import java.util.function.Function

class MainWindowController {

    @FXML
    private lateinit var waitingProcesses: ListView<String>

    @FXML
    private lateinit var interruptedProcesses: ListView<String>

    @FXML
    private lateinit var cycle: Label

    @FXML
    private lateinit var currentProcessName: Label

    private lateinit var cpuScheduler: CpuScheduler

    private var timeline: Timeline? = null

    @FXML
    private fun initialize() {
        cycle.text = "Cycle: 0"
        cpuScheduler = CpuScheduler(generateProcesses())
        updateWaitingProcessesView()
        if (timeline != null) {
            timeline!!.stop()
        }
        timeline = Timeline(
            KeyFrame(Duration.seconds(1.0),
                { nextCycle() })
        )
        timeline?.cycleCount = Animation.INDEFINITE
        timeline?.play()
    }

    @FXML
    private fun onSpeedUp() =
        changeUpdateCycleRate { it.divide(2.0) }

    @FXML
    private fun onSpeedDown() = changeUpdateCycleRate { it.multiply(2.0) }

    @FXML
    private fun onKey(keyEvent: KeyEvent) {
        if (keyEvent.code == KeyCode.R) {
            initialize()
        }
    }

    private fun changeUpdateCycleRate(delayCalculator: Function<Duration, Duration>) {
        if (timeline == null) {
            return
        }
        val frame = timeline!!.keyFrames[0]
        val delay = delayCalculator.apply(frame.time)
        timeline!!.stop()
        timeline = Timeline(KeyFrame(delay, { nextCycle() }))
        timeline!!.cycleCount = Timeline.INDEFINITE
        timeline!!.play()
    }

    private fun updateWaitingProcessesView() {
        waitingProcesses.items.clear()
        cpuScheduler.consumeWaitingProcessesNames(waitingProcesses.items::add)
    }

    private fun updateInterruptedProcesses() {
        interruptedProcesses.items.clear()
        cpuScheduler.consumeInterruptedProcessesNames(interruptedProcesses.items::add)
    }

    @FXML
    private fun onPauseResume() {
        when {
            timeline == null -> initialize()
            timeline!!.status == Animation.Status.PAUSED -> timeline!!.play()
            timeline!!.status == Animation.Status.RUNNING -> timeline!!.pause()
        }
    }

    private fun nextCycle() {
        cpuScheduler.nextCycle()
        cycle.text = "Cycle: " + cpuScheduler.currentCycle
        updateWaitingProcessesView()
        updateInterruptedProcesses()
        currentProcessName.text = cpuScheduler.getCurrentProcessName()
        if (cpuScheduler.hasEnded) {
            timeline!!.stop()
            timeline = null
            openStats()
        }
    }

    private fun openStats() {
        val loader = FXMLLoader(getResourceURL("stats.fxml"))
        loader.setController(
            StatWindowController(
                cpuScheduler.calculateCpuStats(),
                cpuScheduler.calculateProcessStats()
            )
        )
        val root = loader.load<Parent>()
        val scene = Scene(root)
        val stage = Stage()
        stage.scene = scene
        stage.title = "Stats"
        stage.show()
    }
}