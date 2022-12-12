package day12

import streamInput
import java.util.HashSet
import java.util.PriorityQueue

private val file = "Day12"
//private val file = "Day12Example"

data class Point(val x: Int, val y: Int)
data class Parsed(val grid: List<IntArray>, val start: Point, val end: Point)

private fun parseInput(): Parsed {
    return streamInput(file) { lines ->
        val rows = ArrayList<IntArray>()
        var start: Point? = null
        var end: Point? = null
        for ((row, line) in lines.withIndex()) {
            val rowData = IntArray(line.length)
            line.forEachIndexed { col, c ->
                val actualChar = when (c) {
                    'S' -> {
                        start = Point(col, row)
                        'a'
                    }
                    'E' -> {
                        end = Point(col, row)
                        'z'
                    }
                    else -> c
                }
                val i = actualChar - 'a'
                rowData[col] = i
            }
            rows += rowData
        }
        Parsed(rows, start!!, end!!)
    }
}

data class FPoint(val point: Point, val f: Int, val predecessor: FPoint?) : Comparable<FPoint> {

    override fun compareTo(other: FPoint): Int {
        return f.compareTo(other.f)
    }
}

fun Point.withF(f: Int, predecessor: FPoint?): FPoint {
    return FPoint(this, f, predecessor)
}

private fun doAStar(grid: List<IntArray>, start: Point, end: Point): ArrayList<Point>? {
    val openlist = PriorityQueue<FPoint>()
    val closedlist = HashSet<Point>()
    openlist.add(start.withF(0, null))

    val gScore = HashMap<Point, Int>()

    fun Point.successors(): Sequence<Point> {
        return sequence {
            for ((x, y) in listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))) {
                val next = copy(x = this@successors.x + x, y = this@successors.y + y)
                if (next.y in 0..grid.lastIndex && next.x in 0..grid[0].lastIndex) {
                    val myVal = grid[this@successors.y][this@successors.x]
                    val nextVal = grid[next.y][next.x]
                    if (nextVal <= myVal + 1) {
                        yield(next)
                    }
                }
            }
        }
    }

    fun expandNode(node: FPoint) {
        for (successor in node.point.successors()) {
            if (successor in closedlist) continue

            val tentativeG = gScore[node.point]!! + 1
            val successorG = gScore[successor]
            if (successorG != null && tentativeG >= successorG) continue

            gScore[successor] = tentativeG

            val successorF = successor.withF(tentativeG + 1, node)
            if (successorG != null) {
                openlist.removeIf { it.point == successor }
            }
            openlist.add(successorF)
        }
    }

    gScore[start] = 0

    var foundEndFNode: FPoint? = null
    do {
        val currentNode = checkNotNull(openlist.poll())
        if (currentNode.point == end) {
            // done
            foundEndFNode = currentNode
            break
        }
        closedlist.add(currentNode.point)
        expandNode(currentNode)
    } while (openlist.isNotEmpty())

    if (foundEndFNode == null) return null // no path
    val path = ArrayList<Point>()
    var current: FPoint? = foundEndFNode
    while(current != null) {
        path.add(current.point)
        current = current.predecessor
    }
    path.reverse()
    return path
}

private fun part1() {
    val (grid, start, end) = parseInput()
    val path = checkNotNull(doAStar(grid, start, end))
    println(path)
    println(path.size - 1) // steps = points - 1
}

private fun part2() {
    val (grid, _, end) = parseInput()
    val possibleStarts = grid.flatMapIndexed { row: Int, rowData: IntArray ->
        rowData.flatMapIndexed { col, d -> if (d == 0) listOf(Point(col, row)) else listOf() }
    }
    val minPath = possibleStarts.minOf { start ->
        val path = doAStar(grid, start, end)
        if (path != null) path.size - 1 else Int.MAX_VALUE
    }
    println(minPath)
}

fun main() {
    part1()
    part2()
}
