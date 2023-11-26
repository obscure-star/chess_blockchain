package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.fancyPrintln
import chess.toColumn
import chess.toColumnNumber
import kotlin.math.abs

data class BlackPawn(
    override val name: String = "black_pawn",
    override val point: Int = 1,
    override val image: String = " P ",
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

            if (col in 1..8 && newRow in 1..8 && !playerPiecePositions.contains("${col.toColumn()}$newRow") &&
                (
                    (abs(colOffset) == abs(rowOffset) && otherPlayerPiecePositions.contains("${col.toColumn()}$newRow")) ||
                        abs(colOffset) != abs(rowOffset)
                )
            ) {
                validPositions.add(Position(newRow, col.toColumn()))
            }
        }

        // Pawn moves forward
        addIfValid(0, -1)

        // Pawn's initial double move
        if (position.row == 7) {
            addIfValid(0, -2)
        }

        // Pawn captures diagonally
        addIfValid(-1, -1)
        addIfValid(1, -1)

        fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }
}
