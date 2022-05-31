package com.github.haseoo.cpuschedsim.domain

import com.github.haseoo.cpuschedsim.controller.stats.CpuStats
import com.github.haseoo.cpuschedsim.controller.stats.IProcessStats
import com.github.haseoo.cpuschedsim.controller.stats.ProcessStats
import java.util.*

class CpuScheduler(private val processes: List<Process>) {

    private var cycle = 0

    val currentCycle get() = cycle

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

    init {
        this.notStartedProcesses.addAll(processes)
        startProcesses(0)
    }


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

    fun calculateCpuStats(): CpuStats {
        val stats = CpuStats(
            waitingProcessCount.toList(),
            interruptedProcessCount.toList()
        )
        stats.totalCycles = cycle
        stats.idleCycles = idleCycles
        stats.executingCycles = cycle - idleCycles
        stats.contextChangeCount = contextChanges
        stats.averageInterruptedCount = interruptedProcessCount.average()
        stats.averageWaitingProcessCount = waitingProcessCount.average()
        return stats
    }

    fun calculateProcessStats(): Collection<IProcessStats> {
        val cpuProcessesStats = processes.map(Process::calculateStats)
        return cpuProcessesStats + ProcessStats.calculateAverage(cpuProcessesStats)
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
}