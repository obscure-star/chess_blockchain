package chess.common.model.players

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
    override val name: String,
    override var playerPoints: Int = 0,
    override var ownPieces: Set<Piece> = emptySet(),
    override var wonPieces: Set<Piece> = emptySet(),
    override var lostPieces: Set<Piece> = emptySet(),
    override var selectedPiece: Piece? = null,
    override var destinationPiece: Piece? = null,
) : Player {
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
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "b",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "c",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "d",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "e",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "f",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "g",
                        ),
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position =
                        Position(
                            row = 2,
                            column = "h",
                        ),
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
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position =
                        Position(
                            row = 1,
                            column = "b",
                        ),
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position =
                        Position(
                            row = 1,
                            column = "c",
                        ),
                ),
                Piece(
                    name = "${name}_queen",
                    pieceType = Queen(),
                    position =
                        Position(
                            row = 1,
                            column = "d",
                        ),
                ),
                Piece(
                    name = "${name}_king",
                    pieceType = King(),
                    position =
                        Position(
                            row = 1,
                            column = "e",
                        ),
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position =
                        Position(
                            row = 1,
                            column = "f",
                        ),
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position =
                        Position(
                            row = 1,
                            column = "g",
                        ),
                ),
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position =
                        Position(
                            row = 1,
                            column = "h",
                        ),
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

    override fun setSelectedPiece(piece: Piece): Boolean {
        if (ownPiecePositions().any { it == piece.position.toString() || it == piece.position.toString() + "k" }) {
            selectedPiece = piece
            return true
        }
        selectedPiece = null
        return false
    }

    override fun setDestinationPiece(
        piece: Piece,
        otherPlayerPiecePositions: List<String>,
    ): Boolean {
        val selectedPiece = selectedPiece ?: return false
        selectedPiece.setOpenMoves(ownPiecePositions(), otherPlayerPiecePositions)
        if (selectedPiece.openMoves.any { position -> position.toString() == piece.position.toString() }) {
            destinationPiece = piece
            return true
        }
        destinationPiece = null
        return false
    }

    override fun setWonPieces() {
        wonPieces = destinationPiece?.let { wonPieces.plus(it.copy()) } ?: wonPieces
    }

    override fun setLostPieces(destinationPiece: Piece?) {
        if (destinationPiece != null) {
            if (destinationPiece.name != "empty") {
                ownPieces = ownPieces.minus(destinationPiece)
                lostPieces = lostPieces.plus(destinationPiece.copy())
            }
        }
    }

    override fun updatePlayerPoints() {
        playerPoints = playerPoints.plus(destinationPiece?.pieceType?.point ?: 0)
    }

    override fun ownPiecePositions(): List<String> {
        return ownPieces.map { piece: Piece ->
            if (piece.name?.contains("king") == true) {
                piece.position.toString() + "k"
            } else {
                piece.position.toString()
            }
        }
    }
}
