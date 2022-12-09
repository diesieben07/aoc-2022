import kotlin.math.abs
import kotlin.math.sign

private val file = "Day09"
//private val file = "Day09Example"

private enum class Direction(val x: Int, val y: Int) {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0)
}

private data class Step(val direction: Direction, val amount: Int) {

    companion object {
        fun parse(line: String): Step {
            val direction = Direction.values().single { it.name.startsWith(line[0]) }
            val amount = line.substring(2).toInt()
            return Step(direction, amount)
        }
    }

}

private fun part1() {
    val visited = mutableSetOf<Pair<Int, Int>>()

    streamInput(file) { input ->
        var xH = 0
        var yH = 0
        var xT = 0
        var yT = 0
        visited.add(Pair(xT, yT))
        input.map { Step.parse(it) }
            .forEach { step ->
                repeat(step.amount) {
                    xH += step.direction.x
                    yH += step.direction.y
                    val distX = xH - xT
                    val distY = yH - yT
                    val tailNeedsToMove = maxOf(abs(distX), abs(distY)) > 1
                    if (tailNeedsToMove) {
                        xT += distX.sign
                        yT += distY.sign
                    }
                    visited.add(Pair(xT, yT))
                }
            }
    }
    println(visited.size)
}

private class RopePart(var x: Int, var y: Int)

private fun part2() {
    val tailVisited = mutableSetOf<Pair<Int, Int>>()
    val partCount = 10

    streamInput(file) { input ->
        val parts = List(partCount) { RopePart(0, 0) }

        val tail = parts.last()
        val head = parts.first()
        tailVisited.add(Pair(tail.x, tail.y))
        input.map { Step.parse(it) }
            .forEach { step ->
                repeat(step.amount) {
                    head.x += step.direction.x
                    head.y += step.direction.y
                    for (i in 1..parts.lastIndex) {
                        val myHead = parts[i - 1]
                        val me = parts[i]
                        val distX = myHead.x - me.x
                        val distY = myHead.y - me.y
                        val needToMove = maxOf(abs(distX), abs(distY)) > 1
                        if (needToMove) {
                            me.x += distX.sign
                            me.y += distY.sign
                        }
                    }
                    tailVisited.add(Pair(tail.x, tail.y))
                }
            }

        println(tailVisited.size)
    }
}

fun main() {
    part1()
    part2()
}
