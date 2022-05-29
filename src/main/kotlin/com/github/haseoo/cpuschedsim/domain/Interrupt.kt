package com.github.haseoo.cpuschedsim.domain

class Interrupt(val start: Int, val length: Int) : Comparable<Interrupt> {
    private var currentCycle = 0

    fun increment() {
        currentCycle++
    }

    val hasEnded: Boolean
        get() {
            return currentCycle >= length
        }

    override operator fun compareTo(other: Interrupt): Int = start - other.start

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Interrupt

        if (start != other.start) return false
        if (length != other.length) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start
        result = 31 * result + length
        return result
    }


}