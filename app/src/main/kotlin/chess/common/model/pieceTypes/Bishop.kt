package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber

data class Bishop(
    override val name: String = "bishop",
    override val point: Int = 3,
    override val image: String = " B ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addToValidPositions(
            colOffset: Int,
            rowMultiplier: Int,
        ) {
            var newCol = position.column.toColumnNumber() + colOffset + 1
            var newRow = position.row + rowMultiplier * colOffset

            while (newCol in 1..8 && newRow in 1..8 &&
                !playerPiecePositions.contains("${newCol.toColumn()}$newRow")
            ) {
                validPositions.add(Position(newRow, newCol.toColumn()))
                if (otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
                    break
                }
                newCol += colOffset
                newRow += rowMultiplier * colOffset
            }
        }

        // Diagonals: Right and Up, Left and Up, Right and Down, Left and Down
        addToValidPositions(1, -1)
        addToValidPositions(-1, -1)
        addToValidPositions(1, 1)
        addToValidPositions(-1, 1)

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }

    override fun copy(): Bishop {
        return Bishop(name, point, image)
    }
}
