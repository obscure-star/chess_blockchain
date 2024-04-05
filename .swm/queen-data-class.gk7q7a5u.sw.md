---
title: Queen data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/Queen.kt" line="7">

---

This code snippet defines a `Queen` class that implements the `PieceType` interface. It overrides the `movePattern` function to determine the valid positions that a queen can move to on a chessboard. The function takes into account the current position of the queen, the positions of the player's pieces, the positions of the opponent's pieces, and the available open positions for the opponent's pieces. The valid positions are calculated for horizontal, vertical, and diagonal movements.

```kotlin
data class Queen(
    override val name: String = "queen",
    override val point: Int = 9,
    override val image: String = " Q ",
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
                    break // Stop if we encounter our own piece or check
                }

                validPositions.add(Position(newRow, newCol.toColumn()))

                if (otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
                    break // Stop if we encounter other player piece
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

    override fun copy(): Queen {
        return Queen(name, point, image)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
