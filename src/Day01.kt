import kotlin.math.max

private val file = "Day01"

private fun Sequence<String>.parseInput(): Sequence<Int?> {
    return map { it.toIntOrNull() } + null
}

private fun part1() {
    data class State(val maxSoFar: Int = Integer.MIN_VALUE, val accumulator: Int = 0)

    val max = streamInput(file) { lines ->
        lines
            .parseInput()
            .fold(State()) { state, amount ->
                if (amount == null) {
                    State(max(state.maxSoFar, state.accumulator), 0)
                } else {
                    state.copy(accumulator = state.accumulator + amount)
                }
            }.maxSoFar
    }
    println("Max 1 is $max")
}

private fun part2(topN: Int = 3) {
    data class State(val maxEntries: List<Int> = listOf(), val accumulator: Int = 0) {
        fun endOfGroup(): State {
            val index = maxEntries.binarySearch(accumulator)
            if (index < 0) {
                val insertionPoint = -(index + 1)
                val newList = maxEntries.toMutableList()
                newList.add(insertionPoint, accumulator)
                if (newList.size > topN) {
                    newList.removeAt(0)
                }
                return State(newList, 0)
            }
            return copy(accumulator = 0)
        }

        fun addEntry(amount: Int): State {
            return copy(accumulator = amount + accumulator)
        }
    }

    val max = streamInput(file) { lines ->
        lines
            .parseInput()
            .fold(State()) { state, amount ->
                if (amount == null) {
                    state.endOfGroup()
                } else {
                    state.addEntry(amount)
                }
            }.maxEntries.sum()
    }
    println("Max 2 is $max")
}

fun main() {
    part1()
    part2(1)
    part2(3)
}
