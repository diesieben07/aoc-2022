package day16

import streamInput
import java.util.*
import kotlin.math.max

data class Valve(val name: String, val rate: Int, val tunnels: Set<String>)

private val regex = Regex("""Valve (\w+) has flow rate=([\d]+); tunnels? leads? to valves? ((?:\w+(?:, )?)+)""")

fun Sequence<String>.parseInput(): Sequence<Valve> {
    return map { line ->
        val (name, rateR, tunnelsR) = checkNotNull(regex.find(line)).destructured
        val rate = rateR.toInt()
        val tunnels = tunnelsR.splitToSequence(", ").toSet()
        Valve(name, rate, tunnels)
    }
}

private data class FNode<T : Any>(val node: T, val f: Int, val predecessor: FNode<T>?)

fun <T : Any> aStar(
        start: T,
        successors: T.() -> Sequence<T>,
        cost: (T, T) -> Int,
        estimateToTarget: (T) -> Int,
        isTarget: T.() -> Boolean
): List<T>? {
    val openList = PriorityQueue<FNode<T>>(compareBy { it.f })
    val closedList = HashSet<T>()
    val gScore = HashMap<T, Int>()

    fun T.withF(f: Int, predecessor: FNode<T>?) = FNode(this, f, predecessor)

    fun buildPath(end: FNode<T>): List<T> {
        val path = generateSequence(end) { it.predecessor }.map { it.node }.toMutableList()
        path.reverse()
        return path
    }

    openList.add(start.withF(0, null))
    gScore[start] = 0

    do {
        val currentNode = openList.poll()
        if (currentNode.node.isTarget()) {
            return buildPath(currentNode)
        }

        closedList.add(currentNode.node)

        // expand node
        for (successor in currentNode.node.successors()) {
            if (successor in closedList) continue

            val tentativeG = gScore[currentNode.node]!! + cost(currentNode.node, successor)
            val existingFNode = openList.find { it.node == successor }
            if (existingFNode != null && tentativeG >= gScore[successor]!!) continue

            gScore[successor] = tentativeG
            val successorF = successor.withF(tentativeG + estimateToTarget(successor), currentNode)
            if (existingFNode != null) {
                openList.remove(existingFNode)
            }
            openList.add(successorF)

        }
    } while (openList.isNotEmpty())

    return null
}

private fun <T> Collection<T>.allCombinations(): Sequence<Pair<T, T>> {
    return sequence {
        for (a in this@allCombinations) {
            for (b in this@allCombinations) {
                if (a != b) {
                    yield(Pair(a, b))
                }
            }
        }
    }
}

private fun <T> MutableList<T>.swap(a: Int, b: Int) {
    if (a != b) {
        val t = this[a]
        this[a] = this[b]
        this[b] = t
    }
}

private fun part1() {
//    val file = "Day16Example"
    val file = "Day16"

    streamInput(file) { input ->
        val valves = input.parseInput().associateBy { it.name }

        fun Valve.successors(): Sequence<Valve> {
            return tunnels.asSequence().map { valves[it]!! }
        }

        val startValve = checkNotNull(valves["AA"])
        val relevantValves = valves.values.filter { it.rate != 0 }
        check(startValve !in relevantValves)

        val paths = valves.values.allCombinations()
                .associateWith { (from, to) ->
                    aStar(
                            start = from,
                            successors = Valve::successors,
                            cost = { a, b -> 1 },
                            estimateToTarget = { 1 },
                            isTarget = { this == to }
                    )!!
                }

        val workingList = relevantValves.toMutableList()

        var maxScore = 0

        fun permute(start: Int, minutesPassed: Int, scoreSoFar: Int) {
            maxScore = max(maxScore, scoreSoFar)
            if (start != workingList.size) {
                for (i in start until workingList.size) {
                    workingList.swap(i, start)

                    val position = workingList[start]

                    val path = paths[Pair(if (start == 0) startValve else workingList[start - 1], position)]!!
                    val addedDistance = path.size - 1
                    val newMinutesPassed = minutesPassed + addedDistance + 1 // add one minute to open it

                    if (newMinutesPassed <= 30) { // only keep going down the list if we have time left
                        val addedScore = position.rate * (30 - newMinutesPassed)
                        val newScore = scoreSoFar + addedScore

                        permute(start + 1, newMinutesPassed, newScore)
                    }

                    workingList.swap(start, i)
                }
            }
        }

        permute(0, 0, 0)
        println("max score $maxScore")
    }
}

fun main() {
    part1()
}
