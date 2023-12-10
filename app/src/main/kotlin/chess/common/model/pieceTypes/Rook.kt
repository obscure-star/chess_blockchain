package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber

data class Rook(
    override val name: String = "rook",
    override val point: Int = 5,
    override val image: String = " R ",
    var canCastle: Boolean = true,
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
            var newCol = position.column.toColumnNumber() + colOffset + 1
            var newRow = position.row + rowOffset

            while (newCol in 1..8 && newRow in 1..8) {
                if (playerPiecePositions.contains("${newCol.toColumn()}$newRow")
                ) {
                    break // Stop if we encounter our own piece
                }

                validPositions.add(Position(newRow, newCol.toColumn()))

                if (otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
                    break // Stop if we encounter an opponent's piece
                }

                newCol += colOffset
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

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }
}
