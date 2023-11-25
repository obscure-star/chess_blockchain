package common.model.pieceTypes

import chess.toColumn
import chess.toColumnNumber
import common.model.Position

data class Rook(
    override val name: String = "rook",
    override val image: String = " R "
): PieceType{
    override fun movePattern(position: Position, playerPiecePositions: List<String>): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addIfValid(colOffset: Int, rowOffset: Int) {
            var col = position.column.toColumnNumber() + colOffset
            var newRow = position.row + rowOffset

            while (col in 1..8 && newRow in 1..8) {
                if (playerPiecePositions.contains("${col.toColumn()}$newRow")) {
                    break // Stop if we encounter our own piece
                }

                validPositions.add(Position(newRow, col.toColumn()))

                if (playerPiecePositions.contains("${col.toColumn()}$newRow")) {
                    break // Stop if we encounter an opponent's piece
                }

                col += colOffset
                newRow += rowOffset
            }
        }

        // Horizontal and Vertical
        for (offset in -1..1) {
            if (offset != 0) {
                addIfValid(offset, 0) // Horizontal
                addIfValid(0, offset) // Vertical
            }
        }

        println("These are the valid positions: $validPositions")
        return validPositions
    }

}
