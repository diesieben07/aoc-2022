private val file = "Day07"
//private val file = "Day07Example"

private sealed class Instruction {
    data class CD(val target: String) : Instruction()
    object LS : Instruction()
    data class FileSizeInfo(val file: String, val size: Long) : Instruction()
    data class DirectoryInfo(val name: String) : Instruction()

    companion object {
        fun parse(line: String): Instruction {
            return when {
                line.startsWith("$ cd ") -> CD(line.substring(5))
                line == "$ ls" -> LS
                else -> {
                    val (size, file) = line.split(' ', limit = 2)
                    when (size) {
                        "dir" -> DirectoryInfo(file)
                        else -> FileSizeInfo(file, size.toLong())
                    }
                }
            }
        }
    }
}

private class Executor {

    private val currentDir = ArrayDeque<String>()
    val sizesByDir = mutableMapOf<List<String>, Long>()

    fun execute(instruction: Instruction) {
        when (instruction) {
            is Instruction.CD -> {
                when (instruction.target) {
                    ".." -> currentDir.removeLast()
                    else -> currentDir.addLast(instruction.target)
                }
            }

            is Instruction.FileSizeInfo -> {
                for (i in currentDir.indices) {
                    val key = currentDir.subList(0, i + 1).toList()
                    sizesByDir.compute(key) { _, currentSize -> (currentSize ?: 0L) + instruction.size }
                }
            }

            is Instruction.DirectoryInfo -> {}
            is Instruction.LS -> {}
        }
    }

}

private fun executeInput(): Executor {
    return streamInput(file) { input ->
        val executor = Executor()
        input
            .mapNotNull { Instruction.parse(it) }
            .forEach { executor.execute(it) }
        executor
    }
}

private fun part1() {
    val executor = executeInput()
    val sizeAtMost = 100000
    println(
        executor.sizesByDir.values.asSequence()
            .filter { size -> size <= sizeAtMost }
            .sum()
    )
}

private fun part2() {
    val executor = executeInput()

    val diskSpace = 70000000L
    val spaceNeeded = 30000000L
    val freeSpace = diskSpace - executor.sizesByDir[listOf("/")]!!
    val spaceToFree = spaceNeeded - freeSpace

    println(
        executor.sizesByDir.asSequence()
            .filter { e -> e.value >= spaceToFree}
            .minBy { it.value }
    )
}

fun main() {
    part1()
    part2()
}
