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
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position> {
        return emptySet()
    }

    override fun copy(): Empty {
        return Empty(name, point, image)
    }
}
