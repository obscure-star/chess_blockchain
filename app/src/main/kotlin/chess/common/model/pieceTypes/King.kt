package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber

data class King(
    override val name: String = "king",
    override val point: Int? = null,
    override val image: String = " K ",
    val castleRookPositions: MutableList<Position> = mutableListOf(),
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPiecePositions: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        validPositions.addAll(
            castleRookPositions
                .map { rookPosition ->
                    Position(
                        position.row,
                        ((position.column.toColumnNumber() + rookPosition.column.toColumnNumber()) / 2 + 1).toColumn(),
                    )
                }
                .filter { newPosition ->
                    !otherPlayerPiecePositions.contains(newPosition.toString()) &&
                        !playerPiecePositions.contains(newPosition.toString())
                },
        )

        fun addIfValid(
            colOffset: Int,
            rowOffset: Int,
        ) {
            val newCol = position.column.toColumnNumber() + colOffset + 1
            val newRow = position.row + rowOffset

            if (newCol in 1..8 && newRow in 1..8) {
                val newPosition = Position(newRow, newCol.toColumn())
                if (!playerPiecePositions.contains(newPosition.toString()) &&
                    otherPlayerAllOpenPiecePositions.none { it.toString() == newPosition.toString() }
                ) {
                    validPositions.add(newPosition)
                }
            }
        }

        // Horizontal and Vertical
        for (offset in -1..1) {
            for (innerOffset in -1..1) {
                if (offset != 0 || innerOffset != 0) {
                    addIfValid(offset, innerOffset)
                }
            }
        }

        // Diagonals
        for (colOffset in -1..1) {
            for (rowOffset in -1..1) {
                if (colOffset != 0 || rowOffset != 0) {
                    addIfValid(colOffset, rowOffset)
                }
            }
        }

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }
}
