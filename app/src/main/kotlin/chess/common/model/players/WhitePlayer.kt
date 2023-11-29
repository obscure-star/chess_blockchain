package chess.common.model.players

import chess.Color
import chess.common.model.Piece
import chess.common.model.Position
import chess.common.model.pieceTypes.Bishop
import chess.common.model.pieceTypes.King
import chess.common.model.pieceTypes.Knight
import chess.common.model.pieceTypes.Pawn
import chess.common.model.pieceTypes.Queen
import chess.common.model.pieceTypes.Rook
import chess.fancyPrintln

data class WhitePlayer(
    override val name: String = "white",
    override var playerPoints: Int = 0,
    override var ownPieces: Set<Piece> = mutableSetOf(),
    override var wonPieces: Set<Piece> = mutableSetOf(),
    override var lostPieces: Set<Piece> = mutableSetOf(),
    override var selectedPiece: Piece? = null,
    override var destinationPiece: Piece? = null,
    override var allOpenMoves: List<Position> = mutableListOf(),
    override var winner: Boolean = false,
) : Player {
    override fun saveState(): Player {
        // Create a new instance with the same state
        return BlackPlayer(
            name,
            playerPoints,
            ownPieces.map { it.copy() }.toSet(),
            wonPieces.map { it.copy() }.toSet(),
            lostPieces.map { it.copy() }.toSet(),
            selectedPiece?.copy(),
            destinationPiece?.copy(),
            allOpenMoves.map { it.copy() }.toList(),
        )
    }

    override fun restoreState(savedState: Player) {
        playerPoints = savedState.playerPoints
        ownPieces = savedState.ownPieces.map { it.copy() }.toSet()
        wonPieces = savedState.wonPieces.map { it.copy() }.toSet()
        lostPieces = savedState.lostPieces.map { it.copy() }.toSet()
        selectedPiece = savedState.selectedPiece?.copy()
        destinationPiece = savedState.destinationPiece?.copy()
        allOpenMoves = savedState.allOpenMoves.map { it.copy() }.toList()
    }

    override fun defaultPieces(): MutableList<MutableList<Piece>> {
        return mutableListOf(
            mutableListOf(
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "a",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "b",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "c",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "d",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "e",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "f",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "g",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "h",
                        ),
                    color = Color.GREEN.code,
                ),
            ),
            mutableListOf(
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position =
                        Position(
                            row = 1,
                            column = "a",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position =
                        Position(
                            row = 1,
                            column = "b",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position =
                        Position(
                            row = 1,
                            column = "c",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_queen",
                    pieceType = Queen(),
                    position =
                        Position(
                            row = 1,
                            column = "d",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_king",
                    pieceType = King(),
                    position =
                        Position(
                            row = 1,
                            column = "e",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position =
                        Position(
                            row = 1,
                            column = "f",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position =
                        Position(
                            row = 1,
                            column = "g",
                        ),
                    color = Color.GREEN.code,
                ),
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position =
                        Position(
                            row = 1,
                            column = "h",
                        ),
                    color = Color.GREEN.code,
                ),
            ),
        )
    }

    override fun setOwnPieces() {
        if (ownPieces.isEmpty()) {
            ownPieces = defaultPieces().flatten().toSet()
        }
    }

    override fun updateOwnPieces(
        selectedPiece: Piece?,
        destinationPiece: Piece?,
    ) {
        if (selectedPiece == null) {
            fancyPrintln("selected piece is null. Can't update own pieces.")
            return
        }
        if (destinationPiece == null) {
            fancyPrintln("destination piece is null. Can't update own pieces.")
            return
        }
        val selectedPiecePosition = selectedPiece.position.copy()
        val pieceToUpdate = ownPieces.find { it.position == selectedPiecePosition }

        // Check if the piece is found before attempting to update
        if (pieceToUpdate != null) {
            pieceToUpdate.initialPosition = selectedPiecePosition
            pieceToUpdate.position = destinationPiece.position.copy()
            pieceToUpdate.clearOpenMoves()
        }
    }

    override fun updateAllOpenMoves(
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ) {
        allOpenMoves = emptyList()
        for (piece in ownPieces) {
            piece.setOpenMoves(getOwnPiecePositions(), otherPlayerPiecePositions, otherPlayerAllOpenPieces)
            allOpenMoves = allOpenMoves.union(piece.openMoves).toList()
        }
    }

    override fun getInstanceAllOpenMoves(
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): List<Position> {
        var allOpenMoves: List<Position> = emptyList()
        for (piece in ownPieces) {
            val openMoves = piece.getInstanceOpenMoves(getOwnPiecePositions(), otherPlayerPiecePositions, otherPlayerAllOpenPieces)
            allOpenMoves = allOpenMoves.union(openMoves).toList()
        }
        return allOpenMoves
    }

    override fun setSelectedPiece(piece: Piece): Boolean {
        if (getOwnPiecePositions().any { it == piece.position.toString() || it == piece.position.toString() + "k" }) {
            selectedPiece = piece
            return true
        }
        selectedPiece = null
        fancyPrintln("${piece.position} is not your piece.")
        return false
    }

    override fun setDestinationPiece(piece: Piece): Boolean {
        val selectedPiece = selectedPiece ?: return false
        if (selectedPiece.openMoves.any { position -> position.toString() == piece.position.toString() }) {
            destinationPiece = piece
            return true
        }
        fancyPrintln("${piece.position} is not a valid move.")
        destinationPiece = null
        return false
    }

    override fun setWonPieces() {
        wonPieces = destinationPiece?.let { wonPieces.plus(it.copy()) } ?: wonPieces
    }

    override fun setLostPieces(destinationPiece: Piece?) {
        if (destinationPiece != null) {
            if (destinationPiece.name != "empty") {
                ownPieces = ownPieces.minus(ownPieces.find { it.position == destinationPiece.position } ?: Piece())
                lostPieces = lostPieces.plus(destinationPiece.copy())
            }
        }
    }

    override fun updatePlayerPoints() {
        playerPoints = playerPoints.plus(destinationPiece?.pieceType?.point ?: 0)
    }

    override fun getOwnPiecePositions(): List<String> {
        return ownPieces.map { piece: Piece ->
            piece.position.toString()
        }
    }

    override fun setWinner() {
        winner = true
    }
}
