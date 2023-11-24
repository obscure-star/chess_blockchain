package common.model.players

import common.model.Piece
import common.model.Position
import common.model.pieceTypes.*

data class WhitePlayer(
    override val name: String,
    override var points: Long,
    override val ownPieces: List<Piece> = emptyList(),
    override val wonPieces: List<Piece> = emptyList()
): Player {
    override fun defaultPieces(): MutableList<MutableList<Piece>>{
        return mutableListOf(
            mutableListOf(
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position = Position(
                        row = 8,
                        column = "a")
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position = Position(
                        row = 8,
                        column = "b")
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position = Position(
                        row = 8,
                        column = "c")
                ),
                Piece(
                    name = "${name}_queen",
                    pieceType = Queen(),
                    position = Position(
                        row = 8,
                        column = "d")
                ),
                Piece(
                    name = "${name}_king",
                    pieceType = King(),
                    position = Position(
                        row = 8,
                        column = "e")
                ),
                Piece(
                    name = "${name}_bishop",
                    pieceType = Bishop(),
                    position = Position(
                        row = 8,
                        column = "f")
                ),
                Piece(
                    name = "${name}_knight",
                    pieceType = Knight(),
                    position = Position(
                        row = 8,
                        column = "g")
                ),
                Piece(
                    name = "${name}_rook",
                    pieceType = Rook(),
                    position = Position(
                        row = 8,
                        column = "h")
                    ),
                ),
            mutableListOf(
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "a")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "b")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "c")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "d")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "e")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "f")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "g")
                ),
                Piece(
                    name = "${name}_pawn",
                    pieceType = Pawn(),
                    position = Position(
                        row = 7,
                        column = "h")
                ),
            )
        )
    }
}
