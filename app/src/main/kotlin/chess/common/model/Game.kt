package chess.common.model

import chess.checkExitGame
import chess.common.model.players.Player
import chess.fancyPrintln

class Game private constructor(
    val firstPlayer: Player,
    val secondPlayer: Player,
) {
    private var currentPlayer: Player
    private var otherPlayer: Player
    val board = Board()

    init {
        if (firstPlayer.name == "white") {
            currentPlayer = firstPlayer
            otherPlayer = secondPlayer
        } else {
            currentPlayer = secondPlayer
            otherPlayer = firstPlayer
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
        val currentPlayerState = currentPlayer.saveState()
        val otherPlayerState = otherPlayer.saveState()
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
        if (isCheck()) {
            return true
        }
        fancyPrintln("${currentPlayer.selectedPiece?.name} open moves: ${currentPlayer.selectedPiece?.openMoves}")
        updatePlayerPieces()
        updateScores()
        updateDestinationPiece()
        if (leadsToCheck()) {
            currentPlayer.restoreState(currentPlayerState)
            otherPlayer.restoreState(otherPlayerState)
            return true
        }
        fancyPrintln(
            "${currentPlayer.selectedPiece?.name} ${currentPlayer.destinationPiece?.position} " +
                "to ${currentPlayer.selectedPiece?.position} has been played.",
        )
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
        otherPlayer.updateAllOpenMoves(currentPlayer.getOwnPiecePositions(), currentPlayer.allOpenMoves)
        currentPlayer.updateAllOpenMoves(otherPlayer.getOwnPiecePositions(), otherPlayer.allOpenMoves)
        return currentPlayer.setSelectedPiece(selectedPiece) &&
            currentPlayer.setDestinationPiece(destinationPiece)
    }

    private fun isCheck(): Boolean {
        val selectedPiece = currentPlayer.selectedPiece
        if (selectedPiece?.name?.contains("king") == true) {
            return false
        }
        val kingPosition =
            currentPlayer.ownPieces.find {
                    piece: Piece ->
                piece.pieceType.name.contains("king")
            }?.position
        return otherPlayer.allOpenMoves.contains(
            kingPosition,
        ).also {
            if (it) fancyPrintln("You are currently checked. Please move your king at $kingPosition")
        }
    }

    private fun leadsToCheck(): Boolean {
        val selectedPiece = currentPlayer.selectedPiece
        if (selectedPiece?.name?.contains("king") == true) {
            return false
        }
        val kingPosition =
            currentPlayer.ownPieces.find {
                    piece: Piece ->
                piece.pieceType.name.contains("king")
            }?.position
        return otherPlayer.getInstanceAllOpenMoves(currentPlayer.getOwnPiecePositions(), currentPlayer.allOpenMoves).contains(
            kingPosition,
        ).also {
            if (it) fancyPrintln("You will be checked. Please protect your king at $kingPosition")
        }
    }

    private fun updatePlayerPieces() {
        currentPlayer.updateOwnPieces(currentPlayer.selectedPiece, currentPlayer.destinationPiece)
    }

    private fun updateScores() {
        if (currentPlayer.destinationPiece?.name?.contains(otherPlayer.name) == true) {
            otherPlayer.setLostPieces(currentPlayer.destinationPiece)
            currentPlayer.setWonPieces()
            currentPlayer.updatePlayerPoints()
            fancyPrintln(
                "${currentPlayer.name} has gained " +
                    "${currentPlayer.destinationPiece?.pieceType?.point} point. " +
                    "Total point is ${currentPlayer.playerPoints}",
            )
        }
    }

    private fun updateDestinationPiece() {
        // update destination piece to empty
        currentPlayer.destinationPiece?.makeEmpty()
        // update destination piece location
        currentPlayer.selectedPiece?.let { currentPlayer.destinationPiece?.updatePosition(it.initialPosition) }
    }

    private fun updateBoard() {
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
