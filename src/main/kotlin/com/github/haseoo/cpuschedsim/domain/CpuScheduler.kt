package com.github.haseoo.cpuschedsim.domain

import java.util.*

class CpuScheduler(processes: List<Process>) {

    private var cycle = 0
        get

    private val waitingProcesses: Queue<Process> = LinkedList()

    private val notStartedProcesses: MutableList<Process> = ArrayList()

    private val interruptedProcesses: MutableList<Process> = ArrayList()

    private var currentProcess: Process? = null


    private var idleCycles = 0

    private var contextChanges = -1

    private val waitingProcessCount: MutableList<Int> = LinkedList()

    private val interruptedProcessCount: MutableList<Int> = LinkedList()

    val hasEnded: Boolean
        get() = currentProcess == null &&
                waitingProcesses.isEmpty() &&
                interruptedProcesses.isEmpty() &&
                notStartedProcesses.isEmpty()


    fun consumeWaitingProcessesNames(nameConsumer: (String) -> Unit) =
        waitingProcesses.forEach { nameConsumer(it.name) }

    fun consumeInterruptedProcessesNames(nameConsumer: (String) -> Unit) =
        interruptedProcesses.forEach { nameConsumer(it.name) }


    fun getCurrentProcessName() = currentProcess?.name ?: "None"

    fun nextCycle() {
        cycle++
        increaseCycleOfInterruptedProcesses()
        startProcesses(cycle)
        handleCurrentProcess()
        handleInterruptedProcesses()
        handleWaitingProcess()
        if (currentProcess == null) {
            idleCycles++
        }
    }

    private fun handleWaitingProcess() {
        waitingProcessCount.add(waitingProcesses.size)
        waitingProcesses.forEach { it.increaseWaitingCycle() }
    }

    private fun handleInterruptedProcesses() {
        interruptedProcessCount.add(interruptedProcesses.size)
        val interruptedToWaiting = interruptedProcesses
            .filter { it.hasInterruptEnded }
        interruptedToWaiting.forEach { it.endInterrupt() }
        interruptedProcesses.removeAll(interruptedToWaiting)
        waitingProcesses.addAll(interruptedToWaiting)
    }

    private fun increaseCycleOfInterruptedProcesses() =
        interruptedProcesses.forEach { it.increaseInterruptCycle() }

    private fun getProcessFromWaitingPoll(): Process? =
        if (waitingProcesses.isEmpty()) null else waitingProcesses.poll()

    private fun handleCurrentProcess() {
        if (currentProcess == null) {
            changeContext()
        } else {
            currentProcess!!.increaseExecutionCycle()
        }
        if (currentProcess?.isInterrupted == true) {
            interruptedProcesses.add(currentProcess!!)
            changeContext()
        }
        if (currentProcess?.isCompleted == true) {
            currentProcess!!.end = cycle
            changeContext()
        }
    }

    private fun changeContext() {
        currentProcess = getProcessFromWaitingPoll()
        contextChanges++
        currentProcess?.afterContextChange()
    }

    private fun startProcesses(cycle: Int) {
        val processesAtCycle = getProcessesStartingAtCycle(cycle)
        waitingProcesses.addAll(processesAtCycle)
        notStartedProcesses.removeAll(processesAtCycle)
    }

    private fun getProcessesStartingAtCycle(cycle: Int): List<Process> =
        notStartedProcesses.filter { it.start == cycle }

    init {
        this.notStartedProcesses.addAll(processes)
        startProcesses(0)
    }
}