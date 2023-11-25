package chess.common.model.pieceTypes

import chess.common.model.Position

data class Empty(
    override val name: String = "empty",
    override val image: String = " _ ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
    ): Set<Position> {
        return emptySet()
    }
}
