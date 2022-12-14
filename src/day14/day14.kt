package day14

import streamInput

//val file = "Day14Example"
val file = "Day14"

data class Point(val x: Int, val y: Int)

fun Sequence<String>.parseInput(): Sequence<List<Point>> {
    return map { line ->
        line.split(" -> ").map { it.split(',') }.map { (x, y) -> Point(x.toInt(), y.toInt()) }
    }
}

enum class Contents(val display: String) {
    AIR("."), ROCK("#"), SAND("*")
}

private fun part(fakeBottom: Boolean) {
    streamInput(file) { input ->
        val lines = input.parseInput().toMutableList()
        val allPoints = lines.flatten()
        var maxX = allPoints.maxBy { it.x }.x
        var maxY = allPoints.maxBy { it.y }.y

        if (fakeBottom) {
            lines.add(listOf(Point(0, y = maxY + 2), Point(maxX + 300, y = maxY + 2)))
            maxX += 300
            maxY += 2
        }

        val grid = MutableList(maxY + 1) { MutableList(maxX + 1) { Contents.AIR } }
        for (line in lines) {
            for ((start, end) in line.windowed(2)) {
                if (start.x == end.x) {
                    for (y in minOf(start.y, end.y)..maxOf(start.y, end.y)) {
                        grid[y][start.x] = Contents.ROCK
                    }
                } else {
                    for (x in minOf(start.x, end.x)..maxOf(start.x, end.x)) {
                        grid[start.y][x] = Contents.ROCK
                    }
                }
            }
        }

        fun printGrid() {
            println(
                grid.joinToString(separator = "\n") { row ->
                    row.subList(494, row.size).joinToString(separator = "") { it.display }
                }
            )
        }

        fun tryMove(current: Point, dx: Int = 0, dy: Int = 0): Point {
            val n = current.copy(x = current.x + dx, y = current.y + dy)
            return if (n.y in grid.indices && n.x in grid[n.y].indices && grid[n.y][n.x] == Contents.AIR) {
                n
            } else {
                current
            }
        }

        fun oneSandMovement(from: Point): Point? {
            var newPosition = from
            newPosition = tryMove(newPosition, dy = 1)
            if (newPosition == from) {
                newPosition = tryMove(newPosition, dy = 1, dx = -1)
            }
            if (newPosition == from) {
                newPosition = tryMove(newPosition, dy = 1, dx = 1)
            }

            val changed = from != newPosition
            return when {
                changed -> newPosition
                else -> null
            }
        }

        fun simulateSand(from: Point): Point? {
            var currentPosition = from
            do {
                currentPosition = oneSandMovement(currentPosition) ?: break
            } while (true)

            return when {
                currentPosition == from && grid[from.y][from.x] == Contents.AIR -> {
                    grid[from.y][from.x] = Contents.SAND
                    from
                }
                currentPosition != from && currentPosition.y != grid.lastIndex && currentPosition.x != grid[currentPosition.y].lastIndex -> {
                    grid[currentPosition.y][currentPosition.x] = Contents.SAND
                    currentPosition
                }
                else -> null
            }

        }

        val sandStart = Point(500, 0)
        var sandCount = 0
        while (true) {
            val restPosition = simulateSand(sandStart) ?: break
            sandCount++
//            println(sandCount)
//            println(restPosition)
//            printGrid()
//            println()
        }

        println(sandCount)
    }
}

fun main() {
    part(false)
    part(true)
}
