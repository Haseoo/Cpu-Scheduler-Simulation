package com.github.haseoo.cpuschedsim.domain

class Process(
    private val length: Int,
    private val interrupts: Collection<Interrupt>
) {

    var start: Int? = null
        get
        set

    var end: Int? = null
        get
        set

    var name = ""
        get
        set

    private var currentExecutionCycle = 0

    private var currentInterrupt: Interrupt? = null

    private var currentWaitingCycles = 0

    private val waitingCycles: MutableList<Int> = ArrayList()

    private var contextSwitches = -1

    val isCompleted: Boolean get() = currentExecutionCycle >= length

    val hasInterruptEnded: Boolean get() = currentInterrupt?.hasEnded ?: false

    val isInterrupted: Boolean get() = currentInterrupt != null

    val totalInterruptLength: Int get() = interrupts.sumOf { it.length }

    fun increaseWaitingCycle() =
        currentWaitingCycles++

    fun increaseExecutionCycle() {
        currentExecutionCycle++
        currentInterrupt = interrupts.firstOrNull { it.start == currentExecutionCycle }

    }

    fun increaseInterruptCycle() =
        currentInterrupt?.increment()

    fun afterContextChange() {
        waitingCycles.add(currentWaitingCycles)
        currentWaitingCycles = 0
        contextSwitches++
    }

    fun endInterrupt() {
        currentInterrupt = null
    }
}