package chess.common.model.pieceTypes

import chess.common.model.Position

data class Empty(
    override val name: String = "empty",
    override val point: Int = 0,
    override val image: String = " _ ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
    ): Set<Position> {
        return emptySet()
    }

    override fun canCheck(
        newCol: Int,
        newRow: Int,
        otherPlayerPiecePositions: List<String>,
    ): Boolean {
        return false
    }
}
