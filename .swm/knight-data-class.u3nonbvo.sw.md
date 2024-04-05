---
title: Knight data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/Knight.kt" line="8">

---

This code snippet defines a `Knight` class that implements the `PieceType` interface. The `Knight` class represents a chess piece with a name, point value, and an image. It overrides the `movePattern` function, which calculates the valid positions where the knight can move on a chessboard given its current position and the positions of other pieces on the board. The function uses nested loops to iterate over possible knight offsets and adds valid positions to a set. The code also includes a helper function `addIfValid` to check if a position is valid and adds it to the set of valid positions.

```kotlin
data class Knight(
    override val name: String = "knight",
    override val point: Int = 3,
    override val image: String = " N ",
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
            val newCol = position.column.toColumnNumber() + colOffset + 1 // index should start at 1
            val newRow = position.row + rowOffset

            if (newCol in 1..8 && newRow in 1..8 &&
                !playerPiecePositions.contains("${newCol.toColumn()}$newRow")
            ) {
                validPositions.add(Position(newRow, newCol.toColumn()))
            }
        }

        // Knight moves in an "L" shape: two squares in one direction and one square perpendicular to that
        val knightOffsets = listOf(-2, -1, 1, 2)

        for (colOffset in knightOffsets) {
            for (rowOffset in knightOffsets) {
                if (abs(colOffset) != abs(rowOffset)) {
                    addIfValid(colOffset, rowOffset)
                }
            }
        }

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }

    override fun copy(): Knight {
        return Knight(name, point, image)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
