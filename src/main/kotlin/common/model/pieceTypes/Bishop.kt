package common.model.pieceTypes

import common.model.Position
import toColumn
import toColumnNumber

data class Bishop(
    override val name: String = "bishop",
    override val image: String = " B ",
): PieceType{
    override fun movePattern(position: Position, playerPiecePositions: List<String>): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addToValidPositions(colOffset: Int, rowMultiplier: Int) {
            var col = position.column.toColumnNumber() + colOffset
            var newRow = position.row + rowMultiplier * colOffset

            while (col in 1..8 && newRow in 1..8 && !playerPiecePositions.contains("${col.toColumn()}$newRow")) {
                validPositions.add(Position(newRow, col.toColumn()))
                col += colOffset
                newRow += rowMultiplier * colOffset
            }
        }

        // Diagonals: Right and Up, Left and Up, Right and Down, Left and Down
        addToValidPositions(1, -1)
        addToValidPositions(-1, -1)
        addToValidPositions(1, 1)
        addToValidPositions(-1, 1)

        println("These are the valid positions: $validPositions")
        return validPositions
    }
}
