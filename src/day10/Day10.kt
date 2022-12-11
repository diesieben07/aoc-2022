package day10

import streamInput
import java.lang.StringBuilder

private val file = "Day10"
//private val file = "Day10Example"

private sealed interface Instruction {

    fun decompose(): Sequence<Instruction>
    fun nextState(state: CpuState): CpuState

    object NOOP : Instruction {

        override fun decompose(): Sequence<Instruction> {
            return sequenceOf(this)
        }

        override fun nextState(state: CpuState): CpuState {
            return state.copy(cycle = state.cycle + 1)
        }
    }

    data class AddX(val amount: Int) : Instruction {
        override fun decompose(): Sequence<Instruction> {
            return sequence {
                yield(NOOP)
                yield(this@AddX)
            }
        }

        override fun nextState(state: CpuState): CpuState {
            return state.copy(x = state.x + this.amount, cycle = state.cycle + 1)
        }
    }

    companion object {
        fun parse(line: String): Instruction {
            return when {
                line == "noop" -> NOOP
                line.startsWith("addx ") -> AddX(line.substring(5).toInt())
                else -> throw UnsupportedOperationException()
            }
        }
    }

}

private data class CpuState(val x: Int, val cycle: Int)

private fun Sequence<String>.getStates(): Sequence<CpuState> {
    return map { Instruction.parse(it) }
            .flatMap { it.decompose() }
            .runningFold(CpuState(1, 1)) { state, instruction ->
                instruction.nextState(state)
            }
}

private fun part1() {
    streamInput(file) { input ->
        val cycles = setOf(20, 60, 100, 140, 180, 220)

        val states = input.getStates()

        println(states.sumOf { if (it.cycle in cycles) it.x * it.cycle else 0 })
    }
}

private fun part2() {
    streamInput(file) { input ->
        val states = input.getStates()
        val output = StringBuilder()

        for (state in states) {
            val spritePosition = state.x
            val currentPixel = (state.cycle - 1) % 40
            if (currentPixel == 0 && state.cycle > 1) {
                output.append('\n')
            }
            val pixelLit = currentPixel in (spritePosition - 1)..(spritePosition + 1)
            output.append(if (pixelLit) '#' else '.')
        }
        println(output)
    }
}

fun main() {
    part1()
    part2()
}
