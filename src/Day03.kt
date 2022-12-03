private val file = "Day03"

private data class RucksackContents(val contents: String) {

    val firstCompartment: String get() = contents.substring(0, contents.length / 2)
    val secondCompartment: String get() = contents.substring(contents.length / 2)

    fun getCommonItemType(): Char {
        return firstCompartment.toSet().intersect(secondCompartment.toSet()).single()
    }

}

private fun Char.getPriority(): Int {
    return if (this in 'a'..'z') {
        this - 'a' + 1
    } else {
        this - 'A' + 27
    }
}

private fun Sequence<String>.parseInput(): Sequence<RucksackContents> {
    return map { RucksackContents(it) }
}

private fun part1() {
    streamInput(file) { input ->
        println(input.parseInput().map { it.getCommonItemType() }.map { it.getPriority() }.sum())
    }
}

private fun part2() {
    streamInput(file) { input ->
        val result = input.parseInput().chunked(3).map { group ->
            val set = group[0].contents.toHashSet()
            set.retainAll(group[1].contents.toHashSet())
            set.retainAll(group[2].contents.toHashSet())
            set.single()
        }.map { it.getPriority() }.sum()
        println(result)
    }
}

fun main() {
    part1()
    part2()
}
