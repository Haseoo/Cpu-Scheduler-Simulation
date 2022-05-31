package com.github.haseoo.cpuschedsim.controller.stats

interface IProcessStats {
    val totalInterruptedCycles: String
    val totalWaitingCycles: String
    val averageWaitingCycles: String
    val contextSwitches: String
    val executionTime: String
    val name: String
}

class ProcessStats(
    override val name: String,
    private val _totalWaitingCycles: Int,
    private val _averageWaitingCycles: Double,
    private val _contextSwitches: Int,
    private val _executionTime: Int,
    private val _totalInterruptedCycles: Int
): IProcessStats {

    override val totalInterruptedCycles: String get() = _totalInterruptedCycles.toString()

    override val totalWaitingCycles: String get() = _totalWaitingCycles.toString()

    override val averageWaitingCycles: String = String.format("%.2f", _averageWaitingCycles)

    override val contextSwitches: String get() = _contextSwitches.toString()

    override val executionTime: String get() = _executionTime.toString()

    companion object {
        fun calculateAverage(processCollection: Collection<ProcessStats>): ProcessAverageStats =
            ProcessAverageStats(
                processCollection.map { it._totalWaitingCycles }.average(),
                processCollection.map { it._averageWaitingCycles }.average(),
                processCollection.map { it._contextSwitches }.average(),
                processCollection.map { it._executionTime }.average(),
                processCollection.map { it._totalInterruptedCycles }.average()
            )
    }
}

class ProcessAverageStats internal constructor(
    private val _totalWaitingCycles: Double,
    _averageWaitingCycles: Double,
    private val _contextSwitches: Double,
    private val _executionTime: Double,
    private val _interruptedTime: Double
) : IProcessStats {

    override val totalInterruptedCycles: String get() = _interruptedTime.toString()

    override val totalWaitingCycles: String get() = _totalWaitingCycles.toString()

    override val averageWaitingCycles: String = String.format("%.2f", _averageWaitingCycles)

    override val contextSwitches: String get() = _contextSwitches.toString()

    override val executionTime: String get() = _executionTime.toString()

    override val name: String
        get() = "Average"
}