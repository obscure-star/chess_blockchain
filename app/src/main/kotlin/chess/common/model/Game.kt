package chess.common.model

import chess.checkExitGame
import chess.common.model.pieceTypes.Rook
import chess.common.model.players.Player
import chess.fancyPrintln
import chess.toColumnNumber

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
        var castleMove: String? = null
        do {
            fancyPrintln("${currentPlayer.name} has ${currentPlayer.playerPoints} points")
            fancyPrintln("${otherPlayer.name} has ${otherPlayer.playerPoints} points")
            if (isCheck()) {
                updateAllOpenMoves(otherPlayer, currentPlayer)
                if (isCheckMate()) {
                    fancyPrintln("Checkmate! ${otherPlayer.name} has won with ${otherPlayer.playerPoints} points.")
                    otherPlayer.setWinner()
                    return false
                }
            }
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
                castleMove = getCastleMove(move)
            } else {
                fancyPrintln("Please enter a valid move like (e2-e4)")
            }
        } while (!isCorrectInput || !isMoveValid)
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

        if (castleMove in CASTLE_MOVES_MAP) {
            if (castleMove != null) {
                swapRook(castleMove)
            }
        }

        board.printBoard()

        // update open moves based on currentPlayer's open moves
        updateAllOpenMoves(currentPlayer, otherPlayer)

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
            currentPlayer.ownPieces.find { it.position.toString().contains(selectedPosition) }
                ?: return false.also {
                    fancyPrintln("$selectedPosition is an invalid selection")
                }
        val destinationPiece =
            board.board.flatten().find {
                    piece: Piece ->
                piece.position.toString().contains(destinationPosition)
            } ?: return false.also {
                fancyPrintln("$destinationPosition is an invalid destination")
            }

        // update open moves based on otherPlayer's open moves
        updateAllOpenMoves(otherPlayer, currentPlayer)
        return currentPlayer.setSelectedPiece(selectedPiece) &&
            currentPlayer.setDestinationPiece(destinationPiece)
    }

    private fun isCheck(): Boolean {
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

    private fun isCheckMate(): Boolean {
        val kingPiece =
            currentPlayer.ownPieces.find {
                    piece: Piece ->
                piece.pieceType.name.contains("king")
            }
        kingPiece?.setOpenMoves(currentPlayer.getOwnPiecePositions(), otherPlayer.getOwnPiecePositions(), otherPlayer.allOpenMoves)
        return kingPiece?.openMoves?.isEmpty() ?: false
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
                "${currentPlayer.name} will gain " +
                    "${currentPlayer.destinationPiece?.pieceType?.point} point. " +
                    "Total point will be ${currentPlayer.playerPoints}",
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

    private fun updateAllOpenMoves(
        firstUpdatedPlayer: Player,
        secondUpdatedPlayer: Player,
    ) {
        firstUpdatedPlayer.updateAllOpenMoves(secondUpdatedPlayer.getOwnPiecePositions(), secondUpdatedPlayer.allOpenMoves)
        secondUpdatedPlayer.updateAllOpenMoves(firstUpdatedPlayer.getOwnPiecePositions(), firstUpdatedPlayer.allOpenMoves)
    }

    private fun getCastleMove(move: String): String? {
        return CASTLE_MOVES_MAP.keys.find { it == move }
    }

    private fun swapRook(castleMove: String) {
        val rookLocation = CASTLE_MOVES_MAP[castleMove]?.first
        val emptyLocation = CASTLE_MOVES_MAP[castleMove]?.second!!
        val rookPiece =
            currentPlayer.ownPieces.find {
                (
                    it.name.contains("rook") &&
                        it.position.toString() == rookLocation
                ) && (it.pieceType as Rook).canCastle
            } ?: return
        val emptyPiece = board.board[8 - emptyLocation[1].digitToInt()][emptyLocation[0].toColumnNumber()]
        currentPlayer.updateOwnPieces(rookPiece, emptyPiece)
        // update destination piece position
        emptyPiece.updatePosition(rookPiece.initialPosition)
        board.swapPieces(rookPiece, emptyPiece)
        // to do: check if other pieces next to rook are empty
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

        val CASTLE_MOVES_MAP =
            mapOf(
                "e1-c1" to Pair("a1", "d1"),
                "e1-g1" to Pair("h1", "f1"),
                "e8-c8" to Pair("a8", "d8"),
                "e8-g8" to Pair("h8", "f8"),
            )
    }
}
