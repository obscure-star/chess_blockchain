package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber
import kotlin.math.abs

data class Knight(
    override val name: String = "knight",
    override val point: Int = 3,
    override val image: String = " N ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addIfValid(
            colOffset: Int,
            rowOffset: Int,
        ) {
            val newCol = position.column.toColumnNumber() + colOffset + 1 // index should start at 1
            val newRow = position.row + rowOffset

            if (newCol in 1..8 && newRow in 1..8 &&
                !playerPiecePositions.contains("${newCol.toColumn()}$newRow")
            ) {
                validPositions.add(Position(newRow, newCol.toColumn()))
            }
        }

        // Knight moves in an "L" shape: two squares in one direction and one square perpendicular to that
        val knightOffsets = listOf(-2, -1, 1, 2)

        for (colOffset in knightOffsets) {
            for (rowOffset in knightOffsets) {
                if (abs(colOffset) != abs(rowOffset)) {
                    addIfValid(colOffset, rowOffset)
                }
            }
        }

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }

    override fun copy(): Knight {
        return Knight(name, point, image)
    }
}
