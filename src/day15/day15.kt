package day15

import com.google.common.collect.Range
import com.google.common.collect.RangeSet
import com.google.common.collect.TreeRangeSet
import streamInput
import java.util.BitSet
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

//val file = "Day15Example"
val file = "Day15"

data class Point(val x: Long, val y: Long) {
    fun manhattanDist(other: Point): Long {
        return abs(other.x - x) + abs(other.y - y)
    }
}

data class SensorWithBeacon(val s: Point, val b: Point) {
    val m by lazy {
        s.manhattanDist(b)
    }
}

private val regex = Regex("""Sensor at x=(-?\d+), y=(-?\d+): .+ beacon is at x=(-?\d+), y=(-?\d+)""")

fun Sequence<String>.parseInput(): Sequence<SensorWithBeacon> {
    return map { line ->
        val (sx, sy, bx, by) = checkNotNull(regex.find(line)).destructured
        SensorWithBeacon(Point(sx.toLong(), sy.toLong()), Point(bx.toLong(), by.toLong()))
    }
}


private fun part1() {
    streamInput(file) { input ->
        val data = input.parseInput().toList()
        val noBeacons = HashSet<Point>()
        val beacons = data.mapTo(HashSet()) { it.b }
        val interestingY = 2000000L
        for ((s, b) in data) {
            val d = s.manhattanDist(b)
            if (interestingY in (s.y - d)..(s.y + d)) {
                val dXThisRow = d - abs(interestingY - s.y)
                for (x in (s.x - dXThisRow)..(s.x + dXThisRow)) {
                    val p = Point(x, interestingY)
                    if (p !in beacons) {
                        noBeacons.add(p)
                    }
                }
            }
        }
        println(noBeacons.size)
    }
}

private fun part2() {
//            val maxCoord = 20L
    val maxCoord = 4000000L
//    val file = "Day15Example"
    val file = "Day15"
    streamInput(file) { input ->
        val data = input.parseInput().toList()


        val noBeacons = HashMap<Long, RangeSet<Long>>()
        val noBeaconsX = HashMap<Long, RangeSet<Long>>()

        for ((index, d) in data.withIndex()) {
            println("I $index")
            for (y in max(0L, (d.s.y - d.m))..min(maxCoord, (d.s.y + d.m))) {
                val dXThisRow = d.m - abs(y - d.s.y)
                val setThisRow = noBeacons.getOrPut(y) { TreeRangeSet.create() }
                setThisRow.add(Range.closedOpen(max(0, (d.s.x - dXThisRow)), min(maxCoord, (d.s.x + dXThisRow)) + 1))
            }
            for (x in max(0, (d.s.x - d.m))..min(maxCoord, (d.s.x + d.m))) {
                val dYThisRow = d.m - abs(x - d.s.x)
                val setThisCol = noBeaconsX.getOrPut(x) { TreeRangeSet.create() }
                setThisCol.add(Range.closedOpen(max(0, (d.s.y - dYThisRow)), min(maxCoord, (d.s.y + dYThisRow)) + 1))
            }
        }

        val y = (0..maxCoord).single { y -> (noBeacons[y]?.asRanges()?.size ?: 1) != 1 }
        val x = (0..maxCoord).single { x -> (noBeaconsX[x]?.asRanges()?.size ?: 1) != 1 }
        println(x)
        println(y)
        println(x * 4000000L + y)
    }
}

fun main() {
//    part1()
    part2()
}
