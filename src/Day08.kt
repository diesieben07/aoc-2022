import java.util.BitSet
import kotlin.math.max

private val file = "Day08"
//private val file = "Day08Example"


private fun readInput(): Pair<Int, List<IntArray>> {
    val lines = readInput(file)
    val gridSize = lines.size
    check(lines.all { it.length == gridSize })

    val grid = lines.map { it.map { c -> c.digitToInt() }.toIntArray() }
    return Pair(gridSize, grid)
}

private fun part1() {
    val (gridSize, grid) = readInput()

    var visible = BitSet(gridSize * gridSize)
    // check each row first left to right then right to left
    for (row in 0 until gridSize) {
        val rowD = grid[row]
        for (colRange in listOf(0 until gridSize, (gridSize - 1 downTo 0))) {
            var highestThisRow = -1
            for (col in colRange) {
                val bitIdx = row * gridSize + col
                val value = rowD[col]
                if (value > highestThisRow) {
                    visible.set(bitIdx)
                    highestThisRow = value
                }
            }
        }
    }

    // check each col, first top to bottom then bottom to top
    cols@for (col in 0 until gridSize) {
        rowRanges@for (rowRange in listOf(0 until gridSize, (gridSize - 1 downTo 0))) {
            var highestThisCol = -1
            for (row in rowRange) {
                val bitIdx = row * gridSize + col
                val value = grid[row][col]
                if (value > highestThisCol) {
                    visible.set(bitIdx)
                    highestThisCol = value
                }
            }
        }
    }

    println(visible.cardinality())
}

private fun part2() {
    val (gridSize, grid) = readInput()
    var maxScore = 0
    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            var score = 1
            for ((rowDir, colDir) in listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))) {
                var dirScore = 0
                val checkHeight = grid[row][col]
                for (i in 1 until Int.MAX_VALUE) {
                    val checkRow = row + rowDir * i
                    val checkCol = col + colDir * i
                    if (checkRow !in 0 until gridSize || checkCol !in 0 until gridSize) break
                    dirScore++
                    val checkVal = grid[checkRow][checkCol]
                    if (checkVal >= checkHeight) {
                        break
                    }
                }
                score *= dirScore
            }
            maxScore = max(score, maxScore)
        }
    }
    println(maxScore)
}

fun main() {
    part1()
    part2()
}
