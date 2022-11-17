package chess

const val CHAROFFSET = 97

class Board {
    val boardPattern = Regex("[a-h][1-8][a-h][1-8]")
    private var state = mutableListOf<MutableList<String>>()
    private var previousMove = ""

    init {
        state.add(MutableList(8) {" "})
        state.add(MutableList(8) {"W"})
        repeat(4) {
            state.add(MutableList(8) {" "})
        }
        state.add(MutableList(8) {"B"})
        state.add(MutableList(8) {" "})
    }

    private fun printEdge() {
        println("  +---+---+---+---+---+---+---+---+")
    }

    private fun printRow(rowNum: Int) {
        print("$rowNum ")
        for (i in 0..7) {
            print("| ${state[rowNum - 1][i]} ")
        }
        println("|")
    }

    fun printBoard() {
        printEdge()
        for (i in 8 downTo 1) {
            this.printRow(i)
            this.printEdge()
        }
        println("    a   b   c   d   e   f   g   h\n")
    }

    fun checkPawnAtStartPosition(move: String, activePlayer: Int): String {
        val startMoveIndex1 = move.slice(1..1).toInt() - 1
        val startMoveIndex2 = move[0].code - CHAROFFSET
        return if (activePlayer == 0 && this.state[startMoveIndex1][startMoveIndex2] != "W") {
            "No white pawn at ${move.slice(0..1)}"
        } else if (activePlayer == 1 && this.state[startMoveIndex1][startMoveIndex2] != "B") {
            "No black pawn at ${move.slice(0..1)}"
        } else {
            ""
        }
    }

    fun isNormalCapture(move: String, activePlayer: Int): Boolean {
        val startMoveLetter = move.slice(0..0)
        val startMoveNumber = move.slice(1..1).toInt()
        val endMoveNumber = move.slice(3..3).toInt()
        val startMoveIndex2 = move[0].code - CHAROFFSET
        val endMoveIndex1 = endMoveNumber - 1
        val endMoveIndex2 = move[2].code - CHAROFFSET
        if (this.state[endMoveIndex1][endMoveIndex2] == " ") return false
        if (activePlayer == 0 && this.state[endMoveIndex1][endMoveIndex2] == "W") return false
        if (activePlayer == 1 && this.state[endMoveIndex1][endMoveIndex2] == "B") return false
        return if (activePlayer == 0) {
            when (startMoveLetter) {
                "a" -> endMoveNumber - startMoveNumber == 1 &&
                        endMoveIndex2 - startMoveIndex2 == 1 &&
                        this.state[endMoveIndex1][endMoveIndex2] == "B"
                "h" -> endMoveNumber - startMoveNumber == 1 &&
                        endMoveIndex2 - startMoveIndex2 == -1 &&
                        this.state[endMoveIndex1][endMoveIndex2] == "B"
                else -> endMoveNumber - startMoveNumber == 1 &&
                        (endMoveIndex2 - startMoveIndex2 == 1 || endMoveIndex2 - startMoveIndex2 == -1) &&
                        this.state[endMoveIndex1][endMoveIndex2] == "B"
            }
        } else {
            when (startMoveLetter) {
                "a" -> startMoveNumber - endMoveNumber == 1 &&
                        endMoveIndex2 - startMoveIndex2 == 1 &&
                        this.state[endMoveIndex1][endMoveIndex2] == "W"
                "h" -> startMoveNumber - endMoveNumber == 1 &&
                        endMoveIndex2 - startMoveIndex2 == -1 &&
                        this.state[endMoveIndex1][endMoveIndex2] == "W"
                else -> startMoveNumber - endMoveNumber == 1 &&
                        (endMoveIndex2 - startMoveIndex2 == 1 || endMoveIndex2 - startMoveIndex2 == -1) &&
                        this.state[endMoveIndex1][endMoveIndex2] == "W"
            }
        }
    }

    fun isEnPassant(move: String, activePlayer: Int): Boolean {
        val startMoveLetter = move.slice(0..0)
        val startMoveNumber = move.slice(1..1).toInt()
        val endMoveNumber = move.slice(3..3).toInt()
        val startMoveIndex2 = move[0].code - CHAROFFSET
        val endMoveIndex2 = move[2].code - CHAROFFSET
        return if (activePlayer == 0) {
            when (startMoveLetter) {
                "a" -> move == "a5b6" && this.previousMove == "b7b5"
                "h" -> move == "h5g6" && this.previousMove == "g7g5"
                else -> startMoveNumber == 5 &&
                        endMoveNumber == 6 &&
                        (endMoveIndex2 - startMoveIndex2 == 1 || endMoveIndex2 - startMoveIndex2 == -1) &&
                         this.previousMove[0] == move[2] &&
                         this.previousMove[1] == '7' &&
                         this.previousMove[3] == '5'
            }
        } else {
            when (startMoveLetter) {
                "a" -> move == "a4b3" && this.previousMove == "b2b4"
                "h" -> move == "h4g3" && this.previousMove == "g2g4"
                else -> startMoveNumber == 4 &&
                        endMoveNumber == 3 &&
                        (endMoveIndex2 - startMoveIndex2 == 1 || endMoveIndex2 - startMoveIndex2 == -1) &&
                        this.previousMove[0] == move[2] &&
                        this.previousMove[1] == '2' &&
                        this.previousMove[3] == '4'
            }
        }
    }

    fun isValidMove(move: String, activePlayer: Int): Boolean {
        val startMoveNumber = move.slice(1..1).toInt()
        val endMoveNumber = move.slice(3..3).toInt()
        val endMoveIndex1 = endMoveNumber - 1
        val endMoveIndex2 = move[2].code - CHAROFFSET
        if (this.state[endMoveIndex1][endMoveIndex2] != " ") return false
        return if (activePlayer == 0) {
            if (startMoveNumber == 2) {
                endMoveNumber - startMoveNumber in (1..2) && move[0] == move[2]
            } else {
                endMoveNumber - startMoveNumber == 1 && move[0] == move[2]
            }
        } else {
            if (startMoveNumber == 7) {
                startMoveNumber - endMoveNumber in (1..2) && move[0] == move[2]
            } else {
                startMoveNumber - endMoveNumber == 1 && move[0] == move[2]
            }
        }
    }

    fun movePawn(move: String, activePlayer: Int) {
        val startMoveIndex1 = move.slice(1..1).toInt() - 1
        val startMoveIndex2 = move[0].code - CHAROFFSET
        val endMoveIndex1 = move.slice(3..3).toInt() - 1
        val endMoveIndex2 = move[2].code - CHAROFFSET
        this.state[startMoveIndex1][startMoveIndex2] = " "
        this.state[endMoveIndex1][endMoveIndex2] = if (activePlayer == 0) "W" else "B"
        if (this.isEnPassant(move, activePlayer)) {
            val previousEndMoveIndex1 = this.previousMove.slice(3..3).toInt() - 1
            val previousEndMoveIndex2 = this.previousMove[2].code - CHAROFFSET
            this.state[previousEndMoveIndex1][previousEndMoveIndex2] = " "
        }
        this.previousMove = move
    }

    fun isWin(): Boolean {
        val endMoveNumber = this.previousMove.slice(3..3).toInt()
        return endMoveNumber == 8 ||
                endMoveNumber == 1 ||
                this.state.flatten().none { it == "W" } ||
                this.state.flatten().none { it == "B" }
    }

    fun isDraw(activePlayer: Int): Boolean {
        var draw = true
        if (activePlayer == 0) {
            loop@ for (i in 0..6) {
                for (j in 0..7) {
                    if (this.state[i][j] == "W") {
                        if (this.state[i+1][j] == " " ||
                            (j == 0 && this.state[i+1][1] == "B") ||
                            (j == 7 && this.state[i+1][6] == "B") ||
                            (j != 0 && j != 7 && (this.state[i+1][j+1] == "B" || this.state[i+1][j-1] == "B"))
                        ) {
                            draw = false
                            break@loop
                        }
                    }
                }
            }
        }
        if (activePlayer == 1) {
            loop@ for (i in 1..7) {
                for (j in 0..7) {
                    if (this.state[i][j] == "B") {
                        if (this.state[i-1][j] == " " ||
                            (j == 0 && this.state[i-1][1] == "W") ||
                            (j == 7 && this.state[i-1][6] == "W") ||
                            (j != 0 && j != 7 && (this.state[i-1][j+1] == "W" || this.state[i-1][j-1] == "W"))
                        ) {
                            draw = false
                            break@loop
                        }
                    }
                }
            }
        }
        return draw
    }

}
data class Player(val name: String)

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = Player(readln())
    println("Second Player's name:")
    val secondPlayer = Player(readln())
    val players = listOf(firstPlayer, secondPlayer)
    val board = Board()
    board.printBoard()
    var activePlayerIndex = 0
    do {
        println("${players[activePlayerIndex].name}'s turn:")
        val move = readln()
        if (move == "exit") {
            println("Bye!")
        } else {
            if (board.boardPattern.matches(move) && board.checkPawnAtStartPosition(move, activePlayerIndex) != "") {
                println(board.checkPawnAtStartPosition(move, activePlayerIndex))
            } else if (board.boardPattern.matches(move) &&
                (board.isValidMove(move, activePlayerIndex) ||
                    board.isNormalCapture(move, activePlayerIndex) ||
                    board.isEnPassant(move, activePlayerIndex))
            ) {
                board.movePawn(move, activePlayerIndex)
                board.printBoard()
                if (board.isWin()) {
                    println("${if (activePlayerIndex == 0) "White" else "Black"} Wins!")
                    println("Bye!")
                    break
                }
                activePlayerIndex = if (activePlayerIndex == 0) 1 else 0
                if (board.isDraw(activePlayerIndex)) {
                    println("Stalemate!")
                    println("Bye!")
                    break
                }
            } else {
                println("Invalid Input")
            }
        }
    } while (move != "exit")
}