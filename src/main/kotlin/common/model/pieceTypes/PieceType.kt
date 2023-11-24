package common.model.pieceTypes

import common.model.Position

interface PieceType {
    val name: String
    val image: String // can be an actual image
    fun movePattern(position: Position, playerPiecePositions: List<String>): Set<Position>
}