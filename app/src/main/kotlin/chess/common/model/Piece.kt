package chess.common.model

import chess.common.model.pieceTypes.Empty
import chess.common.model.pieceTypes.PieceType

data class Piece(
    var name: String = "empty",
    var pieceType: PieceType = Empty(),
    var initialPosition: Position = Position(1, "a"),
    var position: Position = Position(1, "a"),
    var openMoves: Set<Position> = emptySet(),
    var canCheck: Boolean = false,
) {
    fun setOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ) {
        openMoves =
            pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenPieces)
    }

    fun getInstanceOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): Set<Position> {
        return pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenPieces)
    }

    fun clearOpenMoves() {
        openMoves = emptySet()
    }

    fun makeEmpty() {
        name = "empty"
        pieceType = Empty()
        openMoves = emptySet()
    }

    fun updatePosition(newPosition: Position) {
        position = newPosition
    }
}
