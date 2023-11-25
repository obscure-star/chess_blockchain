package chess.common.model

import chess.common.model.players.Player
import chess.fancyPrintln

class Game(val pickedPlayer: Player, val otherPlayer: Player) {
    private var currentPlayer: Player = pickedPlayer
    private val board = Board()

    fun start() {
        board.buildBoard(pickedPlayer)
        pickedPlayer.setOwnPieces()
        otherPlayer.setOwnPieces()
        // keep game running
        do {
            playerAction()
            fancyPrintln("${currentPlayer.name}'s turn. Press enter to continue. Press q to quit")
        } while (readlnOrNull() != "q")
        fancyPrintln("exiting game :(")
    }

    private fun playerAction() {
        do {
            fancyPrintln("Please enter your move (example: e2-e4): ")
            val move = readlnOrNull()
            val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
            var isMoveValid = false
            if (isCorrectInput) {
                isMoveValid = checkMove(move!!)
            } else {
                fancyPrintln("Please enter a valid move like (e2-e4)")
            }
        } while (!isCorrectInput || !isMoveValid)
        updateBoard()
        updatePlayer()
        board.printBoard()
        // switch current player
        currentPlayer =
            if (currentPlayer.name == "white") {
                otherPlayer
            } else {
                pickedPlayer
            }
    }

    private fun checkMove(move: String): Boolean {
        val selectedPosition = "${move[0]}${move[1]}"
        val destinationPosition = "${move[3]}${move[4]}"
        val selectedPiece =
            currentPlayer.ownPieces.find { it.position.toString() == selectedPosition }
                ?: return false.also {
                    fancyPrintln("$selectedPosition is an invalid selection")
                }
        val destinationPiece =
            board.board.flatten().find {
                    piece: Piece ->
                piece.position.toString() == destinationPosition
            } ?: return false.also {
                fancyPrintln("$selectedPosition is an invalid destination")
            }
        return currentPlayer.setSelectedPiece(selectedPiece) && currentPlayer.setDestinationPiece(destinationPiece)
    }

    private fun updateBoard() {
        fancyPrintln(
            "${currentPlayer.selectedPiece?.name} ${currentPlayer.selectedPiece?.position} " +
                "to ${currentPlayer.destinationPiece?.position} has been played.",
        )
        fancyPrintln("Updating board.")
        board.swapPieces(currentPlayer.selectedPiece, currentPlayer.destinationPiece)
    }

    private fun updatePlayer() {
        currentPlayer.updateOwnPieces(currentPlayer.selectedPiece, currentPlayer.destinationPiece)
    }
}
