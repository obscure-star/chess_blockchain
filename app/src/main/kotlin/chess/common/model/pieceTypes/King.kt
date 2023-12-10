package chess.common.model.pieceTypes

import chess.common.model.Position
import chess.toColumn
import chess.toColumnNumber
import kotlin.math.ceil

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
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun checkInBetweenPieces(
            newKingPosition: Position,
            columnOffsets: List<Int>,
        ): Boolean {
            for (offset in columnOffsets) {
                val newKingPositionOffset = Position(newKingPosition.row, (newKingPosition.column.toColumnNumber() + offset + 1).toColumn())
                if (!otherPlayerPiecePositions.contains(newKingPositionOffset.toString()) &&
                    !playerPiecePositions.contains(newKingPositionOffset.toString())
                ) {
                    // Continue to the next iteration
                    continue
                } else {
                    // Set the variable to false and break out of the loop
                    return false
                }
            }
            return true
        }

        // add castle moves
        validPositions.addAll(
            castleRookPositions
                .map { rookPosition ->
                    Position(
                        position.row,
                        (
                            ceil(
                                (
                                    position.column.toColumnNumber() +
                                        rookPosition.column.toColumnNumber()
                                ).toDouble() / 2,
                            ) + 1
                        ).toInt().toColumn(),
                    )
                }
                .filter { newKingPosition ->
                    val isValid =
                        if (newKingPosition.column == "c") {
                            checkInBetweenPieces(newKingPosition, listOf(-1, 1))
                        } else {
                            checkInBetweenPieces(newKingPosition, listOf(-1))
                        }
                    // Return the value of the boolean variable
                    isValid
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
                    otherPlayerAllOpenMoves.none { it.toString() == newPosition.toString() }
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
