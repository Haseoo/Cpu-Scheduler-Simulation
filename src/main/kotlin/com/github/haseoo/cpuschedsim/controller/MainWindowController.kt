package com.github.haseoo.cpuschedsim.controller

import com.github.haseoo.cpuschedsim.domain.CpuScheduler
import com.github.haseoo.cpuschedsim.generateProcesses
import javafx.fxml.FXML

class MainWindowController {

    @FXML
    private fun initialize() {
        CpuScheduler(generateProcesses())
    }

    @FXML
    private fun onSpeedUp() {
        //TODO
    }

    @FXML
    private fun onSpeedDown() {
        //TODO
    }

    @FXML
    private fun onPauseResume() {
        //TODO
    }
}