package chess.common.model

import chess.checkExitGame
import chess.common.model.players.Player
import chess.fancyPrintln

class Game private constructor(val firstPlayer: Player, val secondPlayer: Player) {
    private var currentPlayer: Player
    private var otherPlayer: Player
    val board = Board()

    init {
        if (firstPlayer.name == "white") {
            currentPlayer = firstPlayer
            otherPlayer = secondPlayer
        } else {
            currentPlayer = secondPlayer
            otherPlayer = secondPlayer
        }
        board.buildBoard()
        firstPlayer.setOwnPieces()
        secondPlayer.setOwnPieces()
    }

    fun start() {
        // keep game running
        do {
            val continueGame = playerAction()
        } while (continueGame)
    }

    private fun playerAction(): Boolean {
        do {
            fancyPrintln("Please enter your move (example: e2-e4): ")
            val move = readlnOrNull()
            if (checkExitGame(move)) {
                fancyPrintln("exiting game :(")
                return false
            }
            val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
            var isMoveValid = false
            if (isCorrectInput) {
                isMoveValid = checkMove(move!!)
            } else {
                fancyPrintln("Please enter a valid move like (e2-e4)")
            }
        } while (!isCorrectInput || !isMoveValid)
        updatePlayerPieces()
        updateScores()
        updateBoard()
        board.printBoard()
        // switch current player
        if (currentPlayer.name == "white") {
            currentPlayer = secondPlayer
            otherPlayer = firstPlayer
        } else {
            currentPlayer = firstPlayer
            otherPlayer = secondPlayer
        }
        fancyPrintln("${currentPlayer.name}'s turn. Press q to quit")
        return true
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

        return currentPlayer.setSelectedPiece(selectedPiece) && currentPlayer.setDestinationPiece(destinationPiece, otherPlayer.ownPiecePositions())
    }

    private fun updatePlayerPieces() {
        currentPlayer.updateOwnPieces(currentPlayer.selectedPiece, currentPlayer.destinationPiece)
    }

    private fun updateScores() {
        if (currentPlayer.destinationPiece?.name?.contains(otherPlayer.name) == true) {
            currentPlayer.setWonPieces()
            currentPlayer.updatePlayerPoints()
            fancyPrintln(
                "${currentPlayer.name} has gained " +
                    "${currentPlayer.destinationPiece?.pieceType?.point} point. " +
                    "Total point is ${currentPlayer.playerPoints}",
            )
            otherPlayer.setLostPieces(currentPlayer.destinationPiece)
            // update destination piece to empty
            currentPlayer.destinationPiece?.makeEmpty()
        }
    }

    private fun updateBoard() {
        fancyPrintln(
            "${currentPlayer.selectedPiece?.name} ${currentPlayer.selectedPiece?.position} " +
                "to ${currentPlayer.destinationPiece?.position} has been played.",
        )
        fancyPrintln("Updating board.")
        board.swapPieces(currentPlayer.selectedPiece, currentPlayer.destinationPiece)
    }

    companion object {
        private var currentGame: Game? = null

        fun startNewGame(
            pickedPlayer: Player,
            otherPlayer: Player,
        ) {
            currentGame = Game(pickedPlayer, otherPlayer)
            currentGame?.start()
        }

        fun getCurrentGame(): Game? {
            return currentGame
        }
    }
}
