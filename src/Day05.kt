private val file = "Day05"


private class CrateStack {

    private val stack: ArrayDeque<Char> = ArrayDeque()

    fun takeTop(): Char {
        return stack.removeFirst()
    }

    fun takeTop(n: Int): List<Char> {
        val subList = stack.subList(0, n)
        val result = subList.toList()
        subList.clear()
        return result
    }

    fun getTop(): Char {
        return stack.first()
    }

    fun addOnTop(crate: Char) {
        stack.addFirst(crate)
    }

    fun addOnTop(crates: List<Char>) {
        stack.addAll(0, crates)
    }

    fun addOnBottom(crate: Char) {
        stack.addLast(crate)
    }

    override fun toString(): String {
        return stack.toString()
    }

}

private data class MoveInstruction(val from: Int, val to: Int, val amount: Int) {

    fun execute(stacks: List<CrateStack>) {
        val source = stacks[from - 1]
        val target = stacks[to - 1]
        repeat(amount) {
            target.addOnTop(source.takeTop())
        }
    }

    fun executeWithBulk(stacks: List<CrateStack>) {
        val source = stacks[from - 1]
        val target = stacks[to - 1]
        target.addOnTop(source.takeTop(amount))
    }

    companion object {

        private val regex = Regex("""move (\d+) from (\d+) to (\d+)""")

        fun parse(line: String): MoveInstruction {
            val match = checkNotNull(regex.matchEntire(line))
            val groupValues = match.groupValues
            return MoveInstruction(groupValues[2].toInt(), groupValues[3].toInt(), groupValues[1].toInt())
        }
    }

}

private fun Sequence<String>.parseInput(): Pair<List<CrateStack>, Sequence<MoveInstruction>> {
    val crateRegex = Regex("""(?:\[(.)\])|( {3})(?: |${'$'})""")
    val stacks = mutableListOf<CrateStack>()
    val iterator = iterator()
    for (line in iterator) {
        if ('[' !in line) break
        for ((index, crateMatch) in crateRegex.findAll(line).withIndex()) {
            for (i in stacks.size..index) {
                stacks.add(CrateStack())
            }
            val crateValue = crateMatch.groupValues[1]
            if (crateValue.isNotEmpty()) {
                stacks[index].addOnBottom(crateValue[0])
            }
        }
    }
    iterator.next()
    val moves = sequence {
        for (line in iterator) {
            yield(MoveInstruction.parse(line))
        }
    }

    return Pair(stacks, moves)
}

private fun solve(mover: MoveInstruction.(List<CrateStack>) -> Unit) {
    streamInput(file) { input ->
        val (stacks, moves) = input.parseInput()

        for (move in moves) {
            move.mover(stacks)
        }
        println(stacks.joinToString(separator = "") { it.getTop().toString() })
    }
}

private fun part1() {
    solve(MoveInstruction::execute)
}

private fun part2() {
    solve(MoveInstruction::executeWithBulk)
}

fun main() {
    part1()
    part2()
}
