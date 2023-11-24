package common.model

import common.model.pieceTypes.Empty
import common.model.players.BlackPlayer
import common.model.players.Player
import common.model.players.WhitePlayer
import fancyPrintln
import toColumn

class Board(val pieces: List<Piece> = emptyList()) {
    val blackPlayer: BlackPlayer = BlackPlayer(
         name = "black",
         points = 0L,
        )
    val whitePlayer: WhitePlayer = WhitePlayer(
        name = "white",
        points = 0L,
    )
    fun buildBoard(pickedPlayer: Player){
        if(pieces.isEmpty()){
            buildDefaultBoard(pickedPlayer)
        }
     }

    private fun buildDefaultBoard(pickedPlayer: Player): List<MutableList<Piece>>{
        val whitePieces = whitePlayer.defaultPieces()
        val blackPieces = blackPlayer.defaultPieces()
        val board = whitePieces + List(4){ row -> MutableList(8) {
            col -> Piece(name = "empty", pieceType = Empty(), position = Position(row+1, col.toColumn()+1))
        } } + blackPieces
        printBoard(board, pickedPlayer)
        return board
    }

    private fun printBoard(board: List<MutableList<Piece>>, pickedPlayer: Player){
        for ((i, row) in board.withIndex()) {
            print(" ${if (pickedPlayer.name == "white") 8 - i else i + 1} | ")
            for (square in row) {
                print(square.pieceType?.image)
            }
            println()
        }
        println("   |  _  _  _  _  _  _  _  _ ")
        println("   |  a  b  c  d  e  f  g  h ")
    }
    fun isPositionValid(position: Position): Boolean {
        TODO()
    }
 }
