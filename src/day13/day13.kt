package day13

import streamInput
import java.io.IOException
import java.util.Comparator
import kotlin.text.StringBuilder

private val file = "Day13"
//private val file = "Day13Example"

sealed interface PacketElement {

    fun toList(): L

    data class C(val value: Int) : PacketElement {
        override fun toList(): L {
            return L(listOf(this))
        }
    }

    data class L(val values: List<PacketElement>) : PacketElement {
        override fun toList(): L {
            return this
        }
    }
}

object Comp : Comparator<PacketElement> {
    override fun compare(o1: PacketElement, o2: PacketElement): Int {
        return when {
            o1 is PacketElement.C && o2 is PacketElement.C -> o1.value - o2.value
            o1 is PacketElement.L && o2 is PacketElement.L -> {
                val leftI = o1.values.iterator()
                val rightI = o2.values.iterator()
                while (leftI.hasNext() && rightI.hasNext()) {
                    val leftV = leftI.next()
                    val rightV = rightI.next()
                    val cmp = compare(leftV, rightV)
                    if (cmp != 0) return cmp
                }
                if (leftI.hasNext()) {
                    if (!rightI.hasNext()) {
                        return 1
                    } else {
                        return 0
                    }
                } else {
                    if (!rightI.hasNext()) {
                        return 0
                    } else {
                        return -1
                    }
                }
            }

            else -> return compare(o1.toList(), o2.toList())
        }
    }
}

fun String.parsePacket(): PacketElement.L {
    check(this@parsePacket[0] == '[')
    val stack = ArrayDeque<MutableList<PacketElement>>()
    var currentNumber = StringBuilder()
    for (c in this@parsePacket) {
        if (c.isDigit()) {
            currentNumber.append(c)
        } else {
            if (currentNumber.isNotEmpty()) {
                stack.last().add(PacketElement.C(currentNumber.toString().toInt()))
                currentNumber.clear()
            }
            if (c == '[') {
                stack.addLast(mutableListOf())
            } else if (c == ']') {
                val done = stack.removeLast()
                if (stack.isNotEmpty()) {
                    stack.last().add(PacketElement.L(done))
                } else {
                    return PacketElement.L(done)
                }
            }
        }
    }
    throw IOException("invalid input")
}

fun Sequence<String>.parseInput(): Sequence<Pair<PacketElement.L, PacketElement.L>> {
    return this.windowed(size = 2, step = 3).map { (a, b) -> Pair(a.parsePacket(), b.parsePacket()) }
}

fun Sequence<String>.parseInput2(): Sequence<PacketElement.L> {
    return this.mapNotNull { if (it.isBlank()) null else it.parsePacket() }
}

private fun part1() {
    println(
        streamInput(file) { input ->
            input.parseInput()
                .mapIndexed { index, (left, right) -> if (Comp.compare(left, right) < 0) index + 1 else 0 }
                .sum()
        }
    )
}

private fun part2() {
    val input = streamInput(file) { it.parseInput2().toMutableList() }
    val divider1 = PacketElement.L(listOf(PacketElement.L(listOf(PacketElement.C(2)))))
    val divider2 = PacketElement.L(listOf(PacketElement.L(listOf(PacketElement.C(6)))))
    input.add(divider1)
    input.add(divider2)
    input.sortWith(Comp)
    println((input.indexOf(divider1) + 1) * (input.indexOf(divider2) + 1))
}

fun main() {
    part1()
    part2()
}
