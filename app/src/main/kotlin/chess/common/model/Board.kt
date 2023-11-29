package chess.common.model

import chess.Color
import chess.common.model.pieceTypes.Empty
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import chess.fancyPrintln
import chess.toColumn
import chess.toColumnNumber

class Board {
    lateinit var board: List<MutableList<Piece>>

    fun buildBoard() {
        val whitePieces =
            WhitePlayer(
                name = "white",
                playerPoints = 0,
            ).defaultPieces()
        val blackPieces =
            BlackPlayer(
                name = "black",
                playerPoints = 0,
            ).defaultPieces()
        board = blackPieces +
            List(4) { row ->
                MutableList(8) {
                        col ->
                    Piece(name = "empty", pieceType = Empty(), position = Position(6 - row, (col + 1).toColumn()))
                }
            } +
            whitePieces
        printBoard()
    }

    fun printBoard() {
        println()
        for ((i, row) in board.withIndex()) {
            print(" ${8 - i} | ")
            for (piece in row) {
                print("${piece.color}${piece.pieceType.image}${Color.RESET.code}")
            }
            println()
        }
        println("   |  _  _  _  _  _  _  _  _ ")
        println("   |  a  b  c  d  e  f  g  h ")
    }

    fun swapPieces(
        selectedPiece: Piece?,
        destinationPiece: Piece?,
    ) {
        if (selectedPiece == null) {
            fancyPrintln("selected piece is null")
            return
        }
        if (destinationPiece == null) {
            fancyPrintln("destination piece is null")
            return
        }
        val initialRow = 8 - (selectedPiece.position.row)
        val initialCol = selectedPiece.position.column.toColumnNumber()

        val finalRow = 8 - (destinationPiece.position.row)
        val finalCol = destinationPiece.position.column.toColumnNumber()

        val initialPiece = board[initialRow][initialCol].copy()
        val finalPiece = board[finalRow][finalCol].copy()

        // swap the positions
        initialPiece.position = Position(row = destinationPiece.position.row, column = destinationPiece.position.column)
        finalPiece.position = Position(row = selectedPiece.position.row, column = selectedPiece.position.column)

        // Swap the pieces on the board
        board[initialRow][initialCol] = finalPiece
        board[finalRow][finalCol] = initialPiece
    }
}
