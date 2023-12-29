package chess.database

import chess.common.model.Board
import chess.common.model.Game
import chess.common.model.Piece
import chess.common.model.pieceTypes.BlackPawn
import chess.common.model.pieceTypes.Empty
import chess.common.model.pieceTypes.King
import chess.common.model.pieceTypes.Pawn
import chess.common.model.players.Player
import chess.toColumn

class ChessDataProcessor(
    val game: Game,
    val board: Board,
    private val currentPlayer: Player,
    private val otherPlayer: Player,
) {
    fun getChessData(round: Int): ChessData {
        val boardRepresentation = evaluateBoardRepresentation()
        val chessData =
            ChessData(
                round = round,
                gameId = game.gameId.toString(),
                boardRepresentation = boardRepresentation,
                boardRepresentationInt = evaluateBoardRepresentationInt(boardRepresentation),
                pieceCount = evaluatePieceCount(),
                legalMoves = evaluateLegalMoves(),
                threatsAndAttacks = evaluateThreatsAndAttacks(),
                pieceActivity = evaluatePieceActivity(),
                kingSafety = evaluateKingSafety(),
                pawnStructure = evaluatePawnStructure(),
                materialBalance = evaluateMaterialBalance(),
                centerControl = evaluateCentralControl(),
                previousMoves = getPreviousMoves(),
                move = "${currentPlayer.selectedPiece?.position}-${currentPlayer.destinationPiece?.position}",
                blackWin = isBlackWinner(),
                whiteWin = isWhiteWinner(),
                winner = getWinner(),
            )
        return chessData
    }

    private fun evaluateBoardRepresentation(): String {
        val boardList = board.board
        return boardList.flatten().joinToString("") { piece ->
            if (piece.pieceType is King) "8" else piece.pieceType.point.toString()
        }
    }

    private fun evaluateBoardRepresentationInt(boardRepresentation: String): Int {
        var output = 0
        for (i in boardRepresentation.indices) {
            output += boardRepresentation[i].digitToInt(radix = 10) * (i + 1)
        }
        return output
    }

    private fun evaluatePieceCount(): String {
        val output = emptyMap<String, Int>().toMutableMap()
        val boardList = board.board
        for (row in boardList) {
            for (piece in row) {
                output[piece.name] = output.getOrDefault(piece.name, 0) + 1
            }
        }
        return output.toString()
    }

    private fun evaluateLegalMoves(): MutableMap<String, MutableList<String>> {
        val output = mutableMapOf(currentPlayer.name to mutableListOf<String>(), otherPlayer.name to mutableListOf())

        fun addMovesToOutput(player: Player) {
            for (piece in player.ownPieces) {
                for (move in piece.openMoves) {
                    output[player.name]?.add("${piece.position}-${move.toStringWithoutP()}")
                }
            }
        }

        addMovesToOutput(currentPlayer)
        addMovesToOutput(otherPlayer)

        return output
    }

    private fun evaluateThreatsAndAttacks(): String {
        val output = mutableMapOf(currentPlayer.name to mutableListOf<String>(), otherPlayer.name to mutableListOf())

        fun addMovesToOutput(
            player: Player,
            otherPlayer: Player,
        ) {
            for (piece in player.ownPieces) {
                if (otherPlayer.allOpenMoves.contains(piece.position)) {
                    output[player.name]?.add("${piece.position}")
                }
            }
        }

        addMovesToOutput(currentPlayer, otherPlayer)
        addMovesToOutput(otherPlayer, currentPlayer)

        return output.toString()
    }

    private fun evaluatePieceActivity(): String {
        val output = mutableMapOf<String, Int>()

        fun addToOutput(player: Player) {
            for (piece in player.ownPieces) {
                val pieceName = piece.name
                output[pieceName] = output.getOrDefault(pieceName, 0) + piece.openMoves.count()
            }
        }

        addToOutput(currentPlayer)
        addToOutput(otherPlayer)

        return output.toString()
    }

    private fun evaluateKingSafety(): String {
        val currentPlayerKing = currentPlayer.ownPieces.find { it.name.contains("king") }
        val otherPlayerKing = otherPlayer.ownPieces.find { it.name.contains("king") }
        val kingSafetyScoreMap = mutableMapOf((currentPlayerKing?.name ?: "white_king") to 0, (otherPlayerKing?.name ?: "black_king") to 0)

        // check castling rights
        fun checkCastling(playerKingPiece: Piece?) {
            if ((playerKingPiece?.pieceType as King).canCastle) {
                kingSafetyScoreMap[playerKingPiece.name] = kingSafetyScoreMap.getOrDefault(playerKingPiece.name, 0) + 1
            }
        }

        // check king attack
        fun checkAttackingKing(
            playerKingPiece: Piece?,
            otherPlayer: Player,
        ) {
            if (otherPlayer.allOpenMoves.contains(playerKingPiece?.position)) {
                if (playerKingPiece != null) {
                    kingSafetyScoreMap[playerKingPiece.name] = kingSafetyScoreMap.getOrDefault(playerKingPiece.name, 0) - 1
                }
            }
        }

        checkCastling(currentPlayerKing)
        checkAttackingKing(currentPlayerKing, otherPlayer)
        checkCastling(otherPlayerKing)
        checkAttackingKing(otherPlayerKing, currentPlayer)
        return kingSafetyScoreMap.toString()
    }

    // Pawn evaluation
    data class PawnEvaluation(val name: String, val chains: List<Set<String>>, val backwardPawns: Set<String>)

    private fun extractPawnInfo(player: Player): Pair<Set<String>, Set<String>> {
        val pawns = player.ownPieces.filter { it.pieceType.name.contains("pawn", ignoreCase = true) }
        val positions = pawns.map { it.position.toStringWithoutP() }.toSet()
        val attacks = pawns.flatMap { piece -> piece.openMoves.map { it.toStringWithoutP() } }.toSet()
        return positions to attacks
    }

    private fun evaluatePawnChains(
        pawnPositions: Set<String>,
        pawnAttacks: Set<String>,
    ): List<Set<String>> {
        return pawnPositions.filter { it in pawnAttacks }.map {
            pawnAttacks intersect pawnPositions
        }
    }

    private fun calculateDoubledPawns(): Set<String> {
        return (0..7).flatMap { row ->
            (0..7).filter { col ->
                board.board[row][col].pieceType is Pawn || board.board[row][col].pieceType is BlackPawn
            }.filter { col ->
                (0..7).any { otherRow ->
                    otherRow != row && (
                        board.board[otherRow][col].pieceType is Pawn ||
                            board.board[otherRow][col].pieceType is BlackPawn
                    )
                }
            }.map { col ->
                "${(col + 1).toColumn()}$row"
            }
        }.toSet()
    }

    private fun calculateBackwardPawns(
        pawnPositions: Set<String>,
        otherPawnPositions: Set<String>,
        pawnAttacks: Set<String>,
    ): Set<String> {
        return pawnPositions.filter { pawnPosition ->
            val pawnRank = pawnPosition[1].digitToInt()
            val enemyPawnsInFront = pawnAttacks intersect otherPawnPositions
            enemyPawnsInFront.isEmpty() && pawnRank != 0
        }.toSet()
    }

    private fun evaluatePawnStructure(): String {
        val (currentPlayerPawnPositions, currentPlayerPawnAttacks) = extractPawnInfo(currentPlayer)
        val (otherPlayerPawnPositions, otherPlayerPawnAttacks) = extractPawnInfo(otherPlayer)

        val currentPlayerPawnChains = evaluatePawnChains(currentPlayerPawnPositions, otherPlayerPawnAttacks)
        val otherPlayerPawnChains = evaluatePawnChains(otherPlayerPawnPositions, currentPlayerPawnAttacks)

        val currentPlayerBackwardPawns =
            calculateBackwardPawns(
                currentPlayerPawnPositions,
                otherPlayerPawnPositions,
                currentPlayerPawnAttacks,
            )
        val otherPlayerBackwardPawns =
            calculateBackwardPawns(
                otherPlayerPawnPositions,
                currentPlayerPawnPositions,
                otherPlayerPawnAttacks,
            )

        val currentPlayerEvaluation = PawnEvaluation(currentPlayer.name, currentPlayerPawnChains, currentPlayerBackwardPawns)
        val otherPlayerEvaluation = PawnEvaluation(otherPlayer.name, otherPlayerPawnChains, otherPlayerBackwardPawns)

        return listOf(currentPlayerEvaluation, otherPlayerEvaluation, mapOf("doublePawn" to calculateDoubledPawns())).toString()
    }

    private fun evaluateMaterialBalance(): String {
        val output = mutableMapOf<String, Int>()

        fun addMaterialBalanceToOutput(player: Player) {
            for (piece in player.ownPieces) {
                output[player.name] = output.getOrDefault(player.name, 0) + (piece.pieceType.point ?: 0)
            }
        }

        addMaterialBalanceToOutput(currentPlayer)
        addMaterialBalanceToOutput(otherPlayer)

        return output.toString()
    }

    private fun evaluateCentralControl(): String {
        val centralSquares = listOf(Pair(3, 3), Pair(4, 3), Pair(3, 4), Pair(4, 4))
        val output = mutableMapOf<String, Int>()

        for (square in centralSquares) {
            val piece = board.board[square.first][square.second]

            if (piece.pieceType !is Empty) {
                // Increment the rating based on the presence of friendly pieces on central squares
                if (piece.name.contains("white")) {
                    output["white"] = output.getOrDefault("white", 0) + 1
                }
                if (piece.name.contains("black")) {
                    output["black"] = output.getOrDefault("black", 0) + 1
                }
            }
        }

        return output.toString()
    }

    private fun getPreviousMoves(): String {
        return game.previousMoves.toString()
    }

    private fun isBlackWinner(): Boolean {
        return currentPlayer.name.contains("black") && currentPlayer.winner ||
            otherPlayer.name.contains("black") && otherPlayer.winner
    }

    private fun isWhiteWinner(): Boolean {
        return currentPlayer.name.contains("white") && currentPlayer.winner ||
            otherPlayer.name.contains("white") && otherPlayer.winner
    }

    private fun getWinner(): Int {
        if (isBlackWinner()) {
            return 0
        }
        if (isWhiteWinner()) {
            return 1
        }
        return -1
    }
}
