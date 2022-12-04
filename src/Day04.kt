private val file = "Day04"


private data class CleaningPair(val a: IntRange, val b: IntRange) {

    fun oneFullyContainsTheOther(): Boolean {
        return b.first in a && b.last in a || a.first in b && a.last in b
    }

    fun rangesOverlap(): Boolean {
        return a.first <= b.last && b.first <= a.last
    }

}

private fun String.parseIntRange(): IntRange {
    val (first, last) = split('-')
    return first.toInt()..last.toInt()
}

private fun Sequence<String>.parseInput(): Sequence<CleaningPair> {
    return map {
        val (a, b) = it.split(',')
        CleaningPair(a.parseIntRange(), b.parseIntRange())
    }
}

private fun part1() {
    streamInput(file) { input ->
        println(input.parseInput().count { it.oneFullyContainsTheOther() })
    }
}

private fun part2() {
    streamInput(file) { input ->
        println(input.parseInput().count { it.rangesOverlap() })
    }
}

fun main() {
    part1()
    part2()
}
