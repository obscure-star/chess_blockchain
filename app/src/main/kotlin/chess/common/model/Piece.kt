package chess.common.model

import chess.common.model.pieceTypes.Empty
import chess.common.model.pieceTypes.PieceType

data class Piece(
    var name: String?,
    var pieceType: PieceType = Empty(),
    var position: Position = Position(1, "a"),
    var openMoves: Set<Position> = emptySet(),
    var restrictedMoves: Set<Position> = emptySet(),
) {
    fun setOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
    ) {
        openMoves = pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions)
    }

    fun clearOpenMoves() {
        openMoves = emptySet()
    }

    fun makeEmpty() {
        name = "empty"
        pieceType = Empty()
        openMoves = emptySet()
        restrictedMoves = emptySet()
    }
}
