package day11

import streamInput

private val file = "Day11"
//private val file = "Day11Example"

sealed interface ItemOperationInput {

    fun getValue(oldValue: Long): Long

    object Old : ItemOperationInput {
        override fun getValue(oldValue: Long): Long {
            return oldValue
        }
    }

    data class Constant(val value: Long): ItemOperationInput {
        override fun getValue(oldValue: Long): Long {
            return value
        }
    }

    companion object {
        fun parse(input: String): ItemOperationInput {
            return when (input) {
                "old" -> Old
                else -> Constant(input.toLong())
            }
        }
    }

}

typealias Operator = Long.(Long) -> Long
val operators = mapOf<String, Operator>("+" to Long::plus, "*" to Long::times)

data class Operation(val lhs: ItemOperationInput, val rhs: ItemOperationInput, val operator: Operator) {
    fun getNewValue(oldValue: Long): Long {
        return this.operator(this.lhs.getValue(oldValue), this.rhs.getValue(oldValue))
    }

    companion object {
        private val regex = Regex("""new = (old|\d+) (\+|\*) (old|\d+)$""")

        fun parse(line: String): Operation {
            val (lhs, op, rhs) = checkNotNull(regex.find(line)).destructured
            return Operation(ItemOperationInput.parse(lhs), ItemOperationInput.parse(rhs), checkNotNull(operators[op]))
        }
    }
}

class Test(val divisibleBy: Long) {

    fun matches(value: Long): Boolean {
        return (value % divisibleBy) == 0L
    }

    companion object {
        private val regex = Regex("""divisible by (\d+)$""")
        fun parse(line: String): Test {
            val (d) = checkNotNull(regex.find(line)).destructured
            return Test(d.toLong())
        }
    }

}

data class TestResult(val targetMonkey: Int) {
    companion object {
        private val regex = Regex("""throw to monkey (\d+)$""")

        fun parse(line: String): TestResult {
            val (d) = checkNotNull(regex.find(line)).destructured
            return TestResult(d.toInt())
        }
    }
}


data class MonkeyState(val index: Int, val items: MutableList<Long>, val operation: Operation, val test: Test, val resultTrue: TestResult, val resultFalse: TestResult) {

    companion object {
        private val indexRegex = Regex("""Monkey (\d+):$""")
        private val itemsRegex = Regex("""Starting items: ((?:\d+(?:, )?)+)$""")

        fun parse(input: Iterator<String>): MonkeyState? {
            if (!input.hasNext()) {
                return null
            }
            val (index) = checkNotNull(indexRegex.find(input.next())).destructured
            val itemsLine = input.next()
            val (items) = checkNotNull(itemsRegex.find(itemsLine)).destructured
            val operation = Operation.parse(input.next())
            val test = Test.parse(input.next())
            val trueResult: TestResult
            val falseResult: TestResult
            val l = input.next()
            if (l.contains("If true")) {
                trueResult = TestResult.parse(l)
                falseResult = TestResult.parse(input.next())
            } else {
                falseResult = TestResult.parse(l)
                trueResult = TestResult.parse(input.next())
            }
            if (input.hasNext()) input.next() // skip blank line
            return MonkeyState(
                    index.toInt(),
                    items.split(", ").mapTo(ArrayList()) { it.toLong() },
                    operation, test, trueResult, falseResult
            )
        }
    }

}

fun runSimulation(divisionBy: Long, rounds: Int, monkeys: List<MonkeyState>): List<Long> {
    val inspectionCounts = MutableList(monkeys.size) { 0L }
    val moduloBy = monkeys.fold(1L) { acc, monkey -> acc * monkey.test.divisibleBy }

    repeat(rounds) {
        for (monkey in monkeys) {
            val listIt = monkey.items.listIterator()
            for (item in listIt) {
                inspectionCounts[monkey.index]++
                val newLevel = (monkey.operation.getNewValue(item) / divisionBy) % moduloBy
                val action = if (monkey.test.matches(newLevel)) monkey.resultTrue else monkey.resultFalse
                listIt.remove()
                monkeys[action.targetMonkey].items.add(newLevel)
            }
        }
    }
    return inspectionCounts
}

private fun part(divisionBy: Long, rounds: Int) {
    streamInput(file) { input ->
        val it = input.iterator()
        val monkeys = generateSequence { MonkeyState.parse(it) }.toList()
        val inspectionCounts = runSimulation(divisionBy, rounds, monkeys)
        val monkeyBusiness = inspectionCounts.sortedDescending().subList(0, 2).reduce(Long::times)
        println(monkeyBusiness)
    }
}

private fun part1() {
    part(3, 20)
}

private fun part2() {
    part(1, 10000)
}

fun main() {
    part1()
    part2()
}
