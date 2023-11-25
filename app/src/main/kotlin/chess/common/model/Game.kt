package common.model

import chess.fancyPrintln
import common.model.players.Player

class Game(val pickedPlayer: Player, val otherPlayer: Player) {
    var currentPlayer: Player = pickedPlayer
    val board = Board()

    fun start(){
        board.buildBoard(pickedPlayer, otherPlayer)
        // keep game running
        do {
            playerAction()
            fancyPrintln("${currentPlayer.name}'s turn. Press enter to continue.")
        } while (readlnOrNull() != "q")
        fancyPrintln("exiting game :(")
    }

    fun playerAction(){
        currentPlayer.setOwnPieces()
        do {
            fancyPrintln("Please enter your move (example: e2-e4): ")
            val move = readlnOrNull()
            val correctInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
            var moveCompleted = false
            if(correctInput){
                moveCompleted = playMove(move!!)
            } else {
                fancyPrintln("Please enter a valid input like (e2-e4)")
            }
        } while (!correctInput || !moveCompleted)
        if (currentPlayer.name == "white"){
            currentPlayer = otherPlayer
        } else {
            currentPlayer = pickedPlayer
        }
    }

    fun playMove(move: String): Boolean {
        val selectedPosition = "${move[0]}${move[1]}"
        val destinationPosition = "${move[3]}${move[4]}"
        val selectedPiece = currentPlayer.ownPieces.find { it.position.toString() == selectedPosition } ?:
            Piece(null, null, position = Position(move[1].toString().toInt(), move[0].toString())).also {
            fancyPrintln("${it.position} Invalid selected piece") }
        selectedPiece.setOpenMoves(currentPlayer.piecePositions())
        if(
            isCurrentPlayerPiece(selectedPosition) &&
            isDestinationValid(destinationPosition, selectedPiece)
        ){
            val destinationPiece = board.board.flatten().find { piece: Piece -> piece.position.toString() == destinationPosition } ?: Piece(null, null)
            fancyPrintln("${selectedPiece.name} ${selectedPosition} to ${destinationPiece.position} has been played.")
            fancyPrintln("Updating board.")
            board.swapPieces(selectedPiece, destinationPiece)
            currentPlayer.updateOwnPieces(selectedPiece, destinationPiece)
            board.printBoard()
            return true
        }
        fancyPrintln("$destinationPosition is not a valid move!")
        return false
    }

    fun isCurrentPlayerPiece(selectedPosition: String): Boolean{
        return currentPlayer.piecePositions().contains(selectedPosition)
    }

    fun isDestinationValid(destinationPosition: String, piece: Piece): Boolean{
        return piece.openMoves.any { position -> position.toString() == destinationPosition }
    }
}