package common.model.players

import common.model.Piece
import common.model.Position
import common.model.pieceTypes.*

data class WhitePlayer(
    override val name: String,
    override var points: Long,
    override var ownPieces: Set<Piece> = emptySet(),
    override var wonPieces: Set<Piece> = emptySet()
): Player {
    override fun defaultPieces(): MutableList<MutableList<Piece>>{
        return mutableListOf(
            mutableListOf(
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "a")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "b")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "c")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "d")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "e")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "f")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "g")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 2,
                        column = "h")
                ),
            ),
            mutableListOf(
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position = Position(
                        row = 1,
                        column = "a")
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position = Position(
                        row = 1,
                        column = "b")
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position = Position(
                        row = 1,
                        column = "c")
                ),

                Piece(
                    name = "${name}_queen",
                    pieceType = Queen(),
                    position = Position(
                        row = 1,
                        column = "d")
                ),
                Piece(
                    name = "${name}_king",
                    pieceType = King(),
                    position = Position(
                        row = 1,
                        column = "e")
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position = Position(
                        row = 1,
                        column = "f")
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position = Position(
                        row = 1,
                        column = "g")
                ),
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position = Position(
                        row = 1,
                        column = "h")
                ),
            ),
        )
    }
    override fun setOwnPieces() {
        if (ownPieces.isEmpty()) {
            ownPieces = defaultPieces().flatten().toSet()
        }
    }

    override fun updateOwnPieces(selectedPiece: Piece, destinationPiece: Piece) {
        val pieceToUpdate = ownPieces.find { it.position == selectedPiece.position }

        // Check if the piece is found before attempting to update
        if (pieceToUpdate != null) {
            pieceToUpdate.position = destinationPiece.position
            pieceToUpdate.clearOpenMoves()
        }
    }

    override fun piecePositions(): List<String> {
        return ownPieces.map {it.position.toString()}
    }
}