package common.model

import common.model.pieceTypes.Empty
import common.model.players.BlackPlayer
import common.model.players.Player
import common.model.players.WhitePlayer
import toColumn
import toColumnNumber

class Board {
    val blackPlayer: BlackPlayer = BlackPlayer(
         name = "black",
         points = 0L,
        )
    val whitePlayer: WhitePlayer = WhitePlayer(
        name = "white",
        points = 0L,
    )
    lateinit var board: List<MutableList<Piece>>

    fun buildBoard(currentPlayer: Player, otherPlayer: Player){
        if(currentPlayer.ownPieces.isEmpty()){
            buildDefaultBoard(currentPlayer)
        }
     }

    private fun buildDefaultBoard(currentPlayer: Player){
        val whitePieces = whitePlayer.defaultPieces()
        val blackPieces = blackPlayer.defaultPieces()
        board = blackPieces + List(4){ row -> MutableList(8) {
            col -> Piece(name = "empty", pieceType = Empty(), position = Position(row+3, (col+1).toColumn()))
        } } + whitePieces
        printBoard(currentPlayer)
    }

    fun printBoard(currentPlayer: Player){
        for ((i, row) in board.withIndex()) {
            print(" ${if (currentPlayer.name == "white") 8 - i else i + 1} | ")
            for (square in row) {
                print(square.pieceType?.image)
            }
            println()
        }
        println("   |  _  _  _  _  _  _  _  _ ")
        println("   |  a  b  c  d  e  f  g  h ")
    }

    fun swapPieces(selectedPiece: Piece, destinationPiece: Piece) {
        val initialRow = 8 - (selectedPiece.position.row)
        val initialCol = selectedPiece.position.column.toColumnNumber()

        val finalRow = 8 - (destinationPiece.position.row)
        val finalCol = destinationPiece.position.column.toColumnNumber()

        val initialPiece = board[initialRow][initialCol]
        val finalPiece = board[finalRow][finalCol]

        // Swap the pieces on the board
        board[initialRow][initialCol] = finalPiece
        board[finalRow][finalCol] = initialPiece
    }
 }
