private val file = "Day02"

private enum class RoundResult(val char: Char, val score: Int) {
    WIN('Z', 6),
    LOOSE('X', 0),
    DRAW('Y', 3);

    companion object {
        fun of(char: Char): RoundResult {
            return values().singleOrNull { it.char == char } ?: throw NoSuchElementException("No element for $char")
        }
    }
}

private enum class Hand(val charOp: Char, val charMe: Char, val score: Int) {
    ROCK('A', 'X', 1),
    PAPER('B', 'Y', 2),
    SCISSORS('C', 'Z', 3);

    fun result(op: Hand): RoundResult {
        if (this == op) {
            return RoundResult.DRAW
        }
        return when (this) {
            ROCK -> if (op == PAPER) RoundResult.LOOSE else RoundResult.WIN
            PAPER -> if (op == ROCK) RoundResult.WIN else RoundResult.LOOSE
            SCISSORS -> if (op == PAPER) RoundResult.WIN else RoundResult.LOOSE
        }
    }

    fun myHandFor(desiredResult: RoundResult): Hand {
        return if (desiredResult == RoundResult.DRAW) {
            this
        } else {
            when (this) {
                ROCK -> when (desiredResult) {
                    RoundResult.WIN -> PAPER
                    else -> SCISSORS
                }

                PAPER -> when (desiredResult) {
                    RoundResult.WIN -> SCISSORS
                    else -> ROCK
                }

                SCISSORS -> when (desiredResult) {
                    RoundResult.WIN -> ROCK
                    else -> PAPER
                }
            }
        }
    }

    companion object {
        fun of(char: Char): Hand {
            return values().singleOrNull { it.charOp == char || it.charMe == char } ?: throw NoSuchElementException("No element for $char")
        }
    }
}

private data class RoundData(val op: Hand, val me: Hand, val desiredResult: RoundResult) {
    fun getScore(): Int {
        return me.result(op).score + me.score
    }
    fun getScore2(): Int {
        val myHand = op.myHandFor(desiredResult)
        return desiredResult.score + myHand.score
    }
}

private fun Sequence<String>.parseInput(): Sequence<RoundData> = map { RoundData(Hand.of(it[0]), Hand.of(it[2]), RoundResult.of(it[2])) }

private fun part1() {
    streamInput(file) { input ->
        println(input.parseInput().map { it.getScore() }.sum())
    }
}

private fun part2() {
    streamInput(file) { input ->
        println(input.parseInput().map { it.getScore2() }.sum())
    }
}

fun main() {
    part1()
    part2()
}
