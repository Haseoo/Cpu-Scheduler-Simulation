package com.github.haseoo.cpuschedsim

import com.github.haseoo.cpuschedsim.domain.Interrupt
import com.github.haseoo.cpuschedsim.domain.Process
import org.apache.commons.math3.distribution.*
import java.util.*
import kotlin.math.roundToInt

fun generateProcesses(): List<Process> {
    val interruptDistribution: RealDistribution = ExponentialDistribution(5.0)
    val processLengthDistribution: RealDistribution = ChiSquaredDistribution(10.0)
    val interruptLengthDistribution: RealDistribution = ChiSquaredDistribution(7.0)
    val numberOfProcessesDistribution: IntegerDistribution = UniformIntegerDistribution(5, 25)

    val numberOfProcesses: Int = numberOfProcessesDistribution.sample()
    val processList = ArrayList<Process>(numberOfProcesses)

    for (i in 0 until numberOfProcesses) {
        val interruptLength = quantize(processLengthDistribution.sample())
        val process = Process(
            interruptLength, generateInterrupts(
                interruptLength,
                interruptDistribution,
                interruptLengthDistribution
            )
        )
        processList.add(process)
    }

    val processStartDistribution = UniformIntegerDistribution(
        0,
        calculateTotalProcessLength(processList) / 4
    )

    processList.forEach { it.start = processStartDistribution.sample() }

    val orderedProcessesList = processList
        .sortedBy { it.start }

    for ((i, _) in orderedProcessesList.indices.withIndex()) {
        orderedProcessesList[i].name = "Process$i"
    }

    return orderedProcessesList
}

fun generateInterrupts(
    processLength: Int,
    interruptDistribution: RealDistribution,
    interruptLengthDistribution: RealDistribution
): Set<Interrupt> {
    val interrupts: TreeSet<Interrupt> = TreeSet<Interrupt>()
    var lastCycle = 1
    while (true) {
        val nextCycle: Int = lastCycle + quantize(interruptDistribution.sample())
        if (nextCycle >= processLength) {
            break
        }
        val interrupt = Interrupt(nextCycle, quantize(interruptLengthDistribution.sample()))
        interrupts.add(interrupt)
        lastCycle = nextCycle
    }
    return interrupts
}

fun quantize(value: Double): Int {
    val rounded = value.roundToInt()
    return if (rounded != 0) rounded else 1
}

fun calculateTotalProcessLength(processes: List<Process>): Int = processes.sumOf { it.totalInterruptLength }