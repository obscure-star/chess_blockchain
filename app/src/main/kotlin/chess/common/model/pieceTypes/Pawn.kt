package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber
import kotlin.math.abs

data class Pawn(
    override val name: String = "pawn",
    override val point: Int = 1,
    override val image: String = " P ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun isKill(
            newCol: Int,
            newRow: Int,
            colOffset: Int,
            rowOffset: Int,
        ): Boolean {
            return (abs(colOffset) == abs(rowOffset) && otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow"))
        }

        fun addIfValid(
            colOffset: Int,
            rowOffset: Int,
        ) {
            val newCol = position.column.toColumnNumber() + colOffset + 1
            val newRow = position.row + rowOffset
            if (newCol in 1..8 && newRow in 1..8) {
                if (!playerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
                    if (isKill(newCol, newRow, colOffset, rowOffset)) {
                        validPositions.add(Position(newRow, newCol.toColumn()))
                    } else if (abs(colOffset) != abs(rowOffset)) {
                        validPositions.add(Position(newRow, newCol.toColumn(), true))
                    }
                }
            }
        }

        // Pawn moves forward
        addIfValid(0, 1)

        // Pawn's initial double move
        if (position.row == 2) {
            addIfValid(0, 2)
        }

        // Pawn captures diagonally
        addIfValid(1, 1)
        addIfValid(-1, 1)

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }
}
