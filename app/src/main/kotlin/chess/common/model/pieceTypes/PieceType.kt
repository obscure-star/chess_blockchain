package chess.common.model.pieceTypes

import chess.common.model.Position

interface PieceType {
    val name: String
    val point: Int?
    val image: String // can be an actual image

    fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
    ): Set<Position>
}
