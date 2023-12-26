package chess.common.model

import chess.checkExitGame
import chess.common.model.pieceTypes.Bishop
import chess.common.model.pieceTypes.Knight
import chess.common.model.pieceTypes.Pawn
import chess.common.model.pieceTypes.Queen
import chess.common.model.pieceTypes.Rook
import chess.common.model.players.Player
import chess.database.ChessData
import chess.database.ChessDataDAO
import chess.database.ChessDataProcessor
import chess.database.SQLConnection
import chess.fancyPrintln
import chess.mlmodels.RandomForestImplementation
import chess.toColumnNumber
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import java.util.UUID

class Game private constructor(
    val firstPlayer: Player,
    val secondPlayer: Player,
) {
    private var currentPlayer: Player
    private var otherPlayer: Player
    val board = Board()
    val previousMoves = mutableListOf<String>()
    val gameId: UUID = UUID.randomUUID()
    private var round: Int = 0
    private var chessData: ChessData? = null

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

    private fun start(withDatabaseConnection: Boolean = false) {
        while (true) {
            currentPlayer.saveState()
            otherPlayer.saveState()
            var castleMove: String?
            fancyPrintln("${currentPlayer.name} has ${currentPlayer.playerPoints} points")
            fancyPrintln("${otherPlayer.name} has ${otherPlayer.playerPoints} points")

            // check if current king is checked
            val isCheck = isCheck()
            if (isCheck) {
                updateAllOpenMoves(otherPlayer, currentPlayer)
                if (isCheckMate()) {
                    fancyPrintln("Checkmate! ${otherPlayer.name} has won with ${otherPlayer.playerPoints} points.")
                    otherPlayer.setWinner()
                    if (withDatabaseConnection) {
                        round += 1
                        chessData = getChessData()
                        chessData?.let { chessDataDAO?.insertChessData(it) }
                    }
                    return
                }
            }

            // enter move
            fancyPrintln("Please enter your move (example: e2-e4): ")
            val move = readlnOrNull()
            if (checkExitGame(move)) {
                fancyPrintln("exiting game :(")
                return
            }
            val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
            if (isCorrectInput) {
                if (!checkMove(move!!)) continue
                castleMove = getCastleMove(move, isCheck)
            } else {
                fancyPrintln("Please enter a valid move like (e2-e4)")
                continue
            }

            // promote pawn if needed
            currentPlayer.selectedPiece?.let { selectedPiece ->
                currentPlayer.destinationPiece?.let { destinationPiece ->
                    processPromotePawn(selectedPiece, destinationPiece)
                }
            }

            fancyPrintln("${currentPlayer.selectedPiece?.name} open moves: ${currentPlayer.selectedPiece?.openMoves}")

            // update pieces and players
            updatePlayerPieces()
            updateScores()
            updateDestinationPiece()

            // if action leads to check restore player states
            if (currentPlayer.selectedPiece?.let { leadsToCheck(it, checkPieceMoves = false) } == true) {
                currentPlayer.restoreState()
                otherPlayer.restoreState()
                continue
            }

            fancyPrintln(
                "${currentPlayer.selectedPiece?.name} ${currentPlayer.destinationPiece?.position} " +
                    "to ${currentPlayer.selectedPiece?.position} has been played.",
            )

            // update previous moves
            previousMoves.add(move)

            // send chess data to database
            if (withDatabaseConnection) {
                chessData?.nextMove = move
                chessData?.let { chessDataDAO?.insertChessData(it) }
            }

            updateBoard()

            // check if move is a castle move
            if (castleMove in CASTLE_MOVES_MAP) {
                if (castleMove != null) {
                    swapRook(castleMove)
                }
            }

            board.printBoard()

            // update open moves based on currentPlayer's open moves
            updateAllOpenMoves(currentPlayer, otherPlayer)

            if (withDatabaseConnection) {
                round += 1
                chessData = getChessData()
            }

            // switch current player
            if (currentPlayer.name == "white") {
                currentPlayer = secondPlayer
                otherPlayer = firstPlayer
            } else {
                currentPlayer = firstPlayer
                otherPlayer = secondPlayer
            }
            fancyPrintln("${currentPlayer.name}'s turn. Press q to quit")
        }
    }

    private fun getChessData(): ChessData? {
        // send data to database (populate chessData)
        return currentGame?.let { game ->
            ChessDataProcessor(
                game,
                board,
                currentPlayer,
                otherPlayer,
            )
        }?.getChessData(round)
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
            if (it) fancyPrintln("You are currently checked. Please protect your king at $kingPosition")
        }
    }

    private fun isCheckMate(): Boolean {
        // check there are open moves that prevent checkmate
        for (piece in currentPlayer.ownPieces) {
            if (piece.openMoves.isNotEmpty()) {
                for (destinationPosition in piece.openMoves) {
                    val destinationPiece =
                        board.board.flatten().find {
                            it.position == destinationPosition
                        }
                    if (!leadsToCheck(piece, destinationPiece, checkPieceMoves = true)) return false
                }
            }
        }
        // check if a piece will block threatening pieces
        return true
    }

    private fun leadsToCheck(
        selectedPiece: Piece,
        destinationPiece: Piece? = null,
        checkPieceMoves: Boolean,
    ): Boolean {
        val kingPosition =
            if (selectedPiece.pieceType.name.contains("king")) {
                destinationPiece?.position
            } else {
                currentPlayer.ownPieces.find { piece: Piece ->
                    piece.pieceType.name.contains("king")
                }?.position
            }

        if (checkPieceMoves) {
            if (destinationPiece != null) {
                currentPlayer.saveState()
                otherPlayer.saveState()
                selectedPiece.saveState()
                destinationPiece.saveState()

                currentPlayer.setSelectedPiece(selectedPiece)
                currentPlayer.setDestinationPiece(destinationPiece)

                updatePlayerPieces()
                otherPlayer.setLostPieces(currentPlayer.destinationPiece)
                updateDestinationPiece()
                updateAllOpenMoves(currentPlayer, otherPlayer)

                return otherPlayer.allOpenMoves.contains(kingPosition).also {
                    currentPlayer.restoreState()
                    otherPlayer.restoreState()
                    selectedPiece.restoreState()
                    destinationPiece.restoreState()
                }
            }
        }

        return otherPlayer.getInstanceAllOpenMoves(currentPlayer.getOwnPiecePositions(), currentPlayer.allOpenMoves).contains(
            kingPosition,
        ).also {
            if (it && !checkPieceMoves) fancyPrintln("You will be checked. Please protect your king at $kingPosition")
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

    private fun processPromotePawn(
        selectedPiece: Piece,
        destinationPiece: Piece,
    ) {
        if (selectedPiece.pieceType is Pawn && (destinationPiece.position.row == 1 || destinationPiece.position.row == 8)) {
            do {
                fancyPrintln("Please upgrade your pawn to a piece (Q, R, N, B)")
                val upgradeSelection = readlnOrNull()
                when (upgradeSelection) {
                    "Q" -> {
                        selectedPiece.name = "${currentPlayer.name}_queen"
                        selectedPiece.pieceType = Queen()
                        break
                    }
                    "R" -> {
                        selectedPiece.name = "${currentPlayer.name}_rook"
                        selectedPiece.pieceType = Rook()
                        break
                    }
                    "N" -> {
                        selectedPiece.name = "${currentPlayer.name}_knight"
                        selectedPiece.pieceType = Knight()
                        break
                    }
                    "B" -> {
                        selectedPiece.name = "${currentPlayer.name}_bishop"
                        selectedPiece.pieceType = Bishop()
                        break
                    }
                    else -> fancyPrintln("Please enter: Q, R, N, or B (e.g. Q)")
                }
            } while (true)
        }
        return
    }

    private fun updateAllOpenMoves(
        firstUpdatedPlayer: Player,
        secondUpdatedPlayer: Player,
    ) {
        firstUpdatedPlayer.updateAllOpenMoves(secondUpdatedPlayer.getOwnPiecePositions(), secondUpdatedPlayer.allOpenMoves)
        secondUpdatedPlayer.updateAllOpenMoves(firstUpdatedPlayer.getOwnPiecePositions(), firstUpdatedPlayer.allOpenMoves)
    }

    private fun getCastleMove(
        move: String,
        isCheck: Boolean,
    ): String? {
        if (!isCheck) {
            return CASTLE_MOVES_MAP.keys.find { it == move }
        }
        return null
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
    }

    companion object {
        private var currentGame: Game? = null
        private var connection: Connection? = null
        private var chessDataDAO: ChessDataDAO? = null

        fun startNewGame(
            pickedPlayer: Player,
            otherPlayer: Player,
            withDatabaseConnection: Boolean = false,
        ) {
            currentGame = Game(pickedPlayer, otherPlayer)
            if (withDatabaseConnection) {
                connection =
                    runBlocking {
                        try {
                            SQLConnection.connection()
                        } catch (e: Exception) {
                            // Handle exceptions, log, or return null as appropriate
                            null
                        }
                    }
                chessDataDAO = connection?.let { ChessDataDAO(it) }
            }
            connection?.let { RandomForestImplementation().implementation(it) }
            currentGame?.start(withDatabaseConnection)
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
