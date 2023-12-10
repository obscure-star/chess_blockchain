package chess.common.model

import chess.Color
import chess.common.model.pieceTypes.Empty
import chess.common.model.pieceTypes.PieceType

data class Piece(
    var name: String = "empty",
    var pieceType: PieceType = Empty(),
    var initialPosition: Position = Position(1, "a"),
    var position: Position = Position(1, "a"),
    var openMoves: Set<Position> = emptySet(),
    var color: String = Color.WHITE.code,
    var previousState: Piece? = null,
) {
    fun saveState() {
        previousState =
            Piece(
                name = name,
                pieceType = pieceType,
                initialPosition = initialPosition.copy(),
                position = position.copy(),
                openMoves = openMoves.map { it.copy() }.toSet(),
                color = color,
            )
    }

    fun restoreState() {
        name = previousState?.name.toString()
        pieceType = previousState?.pieceType ?: Empty()
        initialPosition = previousState?.initialPosition ?: Position(1, "a")
        position = previousState?.position ?: Position(1, "a")
        openMoves = previousState?.openMoves?.map { it.copy() }?.toSet() ?: emptySet()
        color = previousState?.color ?: Color.WHITE.code
    }

    fun setOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ) {
        openMoves =
            pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenMoves)
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
        color = Color.WHITE.code
    }

    fun updatePosition(newPosition: Position) {
        position = newPosition
    }
}
