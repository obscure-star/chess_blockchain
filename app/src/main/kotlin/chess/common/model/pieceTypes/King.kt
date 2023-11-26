package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.fancyPrintln
import chess.toColumn
import chess.toColumnNumber

data class King(
    override val name: String = "king",
    override val point: Int? = null,
    override val image: String = " K ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addIfValid(
            colOffset: Int,
            rowOffset: Int,
        ) {
            val col = position.column.toColumnNumber() + colOffset + 1
            val newRow = position.row + rowOffset

            if (col in 1..8 && newRow in 1..8 && !playerPiecePositions.contains("${col.toColumn()}$newRow")) {
                validPositions.add(Position(newRow, col.toColumn()))
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

        fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }
}
