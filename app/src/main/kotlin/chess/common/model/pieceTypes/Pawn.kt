package common.model.pieceTypes

import chess.toColumn
import chess.toColumnNumber
import common.model.Position

data class Pawn(
    override val name: String = "pawn",
    override val image: String = " P "
): PieceType{
    override fun movePattern(position: Position, playerPiecePositions: List<String>): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addIfValid(colOffset: Int, rowOffset: Int) {
            val col = position.column.toColumnNumber() + colOffset
            val newRow = position.row + rowOffset

            if (col in 1..8 && newRow in 1..8 && !playerPiecePositions.contains("${col.toColumn()}$newRow")) {
                validPositions.add(Position(newRow, (col+1).toColumn()))
            }
        }

        // Pawn moves forward
        addIfValid(0, 1)

        // Pawn's initial double move
        if (position.row == 2) {
            addIfValid(0, 2)
        }

        //  TODO("Add opponents pieces too")
        // Pawn captures diagonally
        //addIfValid(1, 1)
        //addIfValid(-1, 1)

        println("These are the valid positions: $validPositions")
        return validPositions
    }


}
