package com.github.haseoo.cpuschedsim.controller

import com.github.haseoo.cpuschedsim.domain.stats.*
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.scene.chart.*
import javafx.scene.control.TableView
import kotlin.math.max

class StatWindowController(
    private val cpuStats: CpuStats,
    private val processStats: Collection<IProcessStats>
) {
    @FXML
    private lateinit var cpuStatsTable: TableView<CpuStatsRecord>

    @FXML
    private lateinit var processesStatsTable: TableView<IProcessStats>
    
    @FXML
    private lateinit var idleExecCycles: PieChart

    @FXML
    private lateinit var countChart: LineChart<Int, Int>

    @FXML
    private lateinit var processExecChart: StackedBarChart<String, Int>

    @FXML
    private lateinit var averageWTChart: BarChart<String, Double>

    @FXML
    private lateinit var contextSwitchesChart: BarChart<String, Number>

    @FXML
    private fun initialize() {
        cpuStatsTable.items.addAll(cpuStats.records)
        processesStatsTable.items.addAll(processStats)
        idleVsExecChart()
        countChart()
        processExecChart()
        averageWaitingTime()
        contextSwitches()
    }

    private fun contextSwitches() {
        contextSwitchesChart.xAxis.label = "Process name"
        contextSwitchesChart.yAxis.label = "Number of context switches"
        val xAxis = processExecChart.xAxis as CategoryAxis
        xAxis.categories = processStats
            .map { it.name }
            .toCollection(FXCollections.observableArrayList())
        val series = XYChart.Series<String, Number>()
        series.name = "Number of context switches"
        processStats.forEach {
            series.data.add(
                XYChart.Data(
                    it.name,
                    if (it.name == "Average") it.contextSwitches.toDouble() else it.contextSwitches.toInt()
                )
            )
        }
        contextSwitchesChart.data.add(series)
        contextSwitchesChart.prefWidth = (processStats.size * 75).toDouble()
    }

    private fun averageWaitingTime() {
        averageWTChart.yAxis.label = "Cycles"
        averageWTChart.xAxis.label = "Process name"
        val xAxis = averageWTChart.xAxis as CategoryAxis
        xAxis.categories = processStats
            .map(IProcessStats::name)
            .toCollection(
                FXCollections.observableArrayList()
            )
        val series = XYChart.Series<String, Double>()
        series.name = "Average waiting time"
        processStats.forEach {
            series.data.add(XYChart.Data(it.name, it.averageWaitingCycles.toDouble()))
        }
        averageWTChart.data.add(series)
        averageWTChart.prefWidth = (processStats.size * 75).toDouble()
    }

    private fun processExecChart() {
        processExecChart.yAxis.label = "Cycles"
        processExecChart.xAxis.label = "Process name"
        val xAxis = processExecChart.xAxis as CategoryAxis
        xAxis.categories = processStats
            .map(IProcessStats::name)
            .toCollection(
                FXCollections.observableArrayList()
            )
        val waitingSeries = XYChart.Series<String, Int>()
        waitingSeries.name = "Waiting cycles"
        val interruptedSeries = XYChart.Series<String, Int>()
        interruptedSeries.name = "Interrupted cycles"
        val execSeries = XYChart.Series<String, Int>()
        execSeries.name = "Execution cycles"
        processStats.forEach {
            val executionTime: Int
            val waitingTime: Int
            val interruptedTime: Int
            if (it.name == "Average") {
                executionTime = it.executionTime.toDouble().toInt()
                waitingTime = it.totalWaitingCycles.toDouble().toInt()
                interruptedTime = it.totalInterruptedCycles.toDouble().toInt()
            } else {
                executionTime = it.executionTime.toInt()
                waitingTime = it.totalWaitingCycles.toInt()
                interruptedTime = it.totalInterruptedCycles.toInt()
            }
            waitingSeries.data.add(
                XYChart.Data(
                    it.name,
                    waitingTime
                )
            )
            interruptedSeries.data.add(XYChart.Data(it.name, interruptedTime))
            execSeries.data.add(
                XYChart.Data(
                    it.name,
                    max(executionTime - interruptedTime - waitingTime, 0)
                )
            )
        }
        processExecChart.data.add(waitingSeries)
        processExecChart.data.add(interruptedSeries)
        processExecChart.data.add(execSeries)
        processExecChart.prefWidth = (processStats.size * 75).toDouble()
    }

    private fun countChart() {
        val waitingProcessCount = cpuStats.waitingProcessCount
        val waitingSeries = XYChart.Series<Int, Int>()
        waitingProcessCount.forEachIndexed { index, value ->
            waitingSeries.data.add(XYChart.Data(index, value))
        }
        waitingSeries.name = "Waiting processes"
        val interruptedProcessCount = cpuStats.interruptedProcessCount
        val interruptedSeries = XYChart.Series<Int, Int>()
        interruptedProcessCount.forEachIndexed { index, value ->
            interruptedSeries.data.add(XYChart.Data(index, value))
        }
        interruptedSeries.name = "interrupted processes"
        countChart.data.add(waitingSeries)
        countChart.data.add(interruptedSeries)
        countChart.prefWidth = max(cpuStats.totalCycles * 3, 600).toDouble()
        countChart.xAxis.label = "Number of processes"
        countChart.yAxis.label = "Cycle"
        countChart.createSymbols = false
    }

    private fun idleVsExecChart() {
        idleExecCycles.data.addAll(
            PieChart.Data(EXECUTING_CYCLES, cpuStats.executingCycles.toDouble()),
            PieChart.Data(IDLE_CYCLES, cpuStats.idleCycles.toDouble())
        )
        idleExecCycles.data.forEach {
            it.nameProperty().bind(
                Bindings.concat(
                    it.name,
                    " ", String.format("%.2f %%", it.pieValueProperty().get() / cpuStats.totalCycles * 100)
                )
            )
        }
    }
}