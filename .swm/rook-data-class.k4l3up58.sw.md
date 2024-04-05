---
title: Rook data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/Rook.kt" line="7">

---

This code snippet defines a `Rook` class that represents a rook chess piece. It implements the `PieceType` interface and overrides its `movePattern` function. The `movePattern` function calculates the valid positions that the rook can move to on a chessboard, given its current position and the positions of other pieces on the board. The valid positions are stored in a set and returned by the function. The `Rook` class also has properties for the name, point value, and image representation of the rook, as well as a boolean property to track whether the rook can still castle.

```kotlin
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

    override fun copy(): Rook {
        return Rook(name, point, image, canCastle)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
