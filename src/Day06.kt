private val file = "Day06"

private fun solve(windowSize: Int) {
    val line = streamInput(file) { input -> input.single() }
    val index = line.asSequence()
        .windowed(windowSize, 1) { window -> window.toSet().size }
        .withIndex()
        .first { (_, size) -> size == windowSize }
        .index
    println(index + windowSize)
}

private fun part1() {
    solve(4)
}

private fun part2() {
    solve(14)
}

fun main() {
    part1()
    part2()
}
