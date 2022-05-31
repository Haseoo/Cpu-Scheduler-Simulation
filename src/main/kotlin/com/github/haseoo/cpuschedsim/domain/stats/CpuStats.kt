package com.github.haseoo.cpuschedsim.domain.stats

const val EXECUTING_CYCLES = "Executing cycles"
const val IDLE_CYCLES = "Idle cycles"
const val TOTAL_CYCLES = "Total cycles"
const val AVERAGE_WAITING_PROCESS_COUNT = "Average waiting process count"
const val AVERAGE_INTERRUPTED_PROCESS_COUNT = "Average interrupted process count"
const val TOTAL_CONTEXT_CHANGES = "Total context changes"

class CpuStatsRecord(val name: String, val value: String)

class CpuStats(val waitingProcessCount: List<Int>, val interruptedProcessCount: List<Int>) {
    private val _records: MutableMap<String, CpuStatsRecord> = HashMap()

    var executingCycles: Int
        get() = _records[EXECUTING_CYCLES]!!.value.toInt()
        set(value) {
            _records[EXECUTING_CYCLES] = CpuStatsRecord(EXECUTING_CYCLES, value.toString())
        }

    var idleCycles: Int
        get() = _records[IDLE_CYCLES]!!.value.toInt()
        set(value) {
            _records[IDLE_CYCLES] = CpuStatsRecord(IDLE_CYCLES, value.toString())
        }

    var totalCycles: Int
        get() = _records[TOTAL_CYCLES]!!.value.toInt()
        set(value) {
            _records[TOTAL_CYCLES] = CpuStatsRecord(TOTAL_CYCLES, value.toString())
        }

    var averageWaitingProcessCount: Double
        get() = _records[AVERAGE_WAITING_PROCESS_COUNT]!!.value.toDouble()
        set(value) {
            _records[AVERAGE_WAITING_PROCESS_COUNT] =
                CpuStatsRecord(AVERAGE_WAITING_PROCESS_COUNT, String.format("%.2f", value))
        }

    var averageInterruptedCount: Double
        get() = _records[AVERAGE_INTERRUPTED_PROCESS_COUNT]!!.value.toDouble()
        set(value) {
            _records[AVERAGE_INTERRUPTED_PROCESS_COUNT] =
                CpuStatsRecord(AVERAGE_INTERRUPTED_PROCESS_COUNT, String.format("%.2f", value))
        }

    var contextChangeCount: Int
        get() = _records[TOTAL_CONTEXT_CHANGES]!!.value.toInt()
        set(value) {
            _records[TOTAL_CONTEXT_CHANGES] =
                CpuStatsRecord(TOTAL_CONTEXT_CHANGES, value.toString())
        }

    val records: Collection<CpuStatsRecord> get() = _records.values.toSet()
}