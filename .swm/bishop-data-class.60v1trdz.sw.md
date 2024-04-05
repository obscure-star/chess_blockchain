---
title: Bishop data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/Bishop.kt" line="7">

---

This code defines a `Bishop` class that represents a bishop chess piece. It implements the `PieceType` interface and overrides its methods. The `movePattern` function calculates the valid positions where the bishop can move on the chessboard, considering other player's pieces and the boundaries of the board. It uses helper functions `addToValidPositions` to add valid positions to a set, based on diagonal movements in four directions. The function returns a set of `Position` objects representing the valid positions for the bishop's movement.

```kotlin
data class Bishop(
    override val name: String = "bishop",
    override val point: Int = 3,
    override val image: String = " B ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position> {
        val validPositions = mutableSetOf<Position>()

        fun addToValidPositions(
            colOffset: Int,
            rowMultiplier: Int,
        ) {
            var newCol = position.column.toColumnNumber() + colOffset + 1
            var newRow = position.row + rowMultiplier * colOffset

            while (newCol in 1..8 && newRow in 1..8 &&
                !playerPiecePositions.contains("${newCol.toColumn()}$newRow")
            ) {
                validPositions.add(Position(newRow, newCol.toColumn()))
                if (otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
                    break
                }
                newCol += colOffset
                newRow += rowMultiplier * colOffset
            }
        }

        // Diagonals: Right and Up, Left and Up, Right and Down, Left and Down
        addToValidPositions(1, -1)
        addToValidPositions(-1, -1)
        addToValidPositions(1, 1)
        addToValidPositions(-1, 1)

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }

    override fun copy(): Bishop {
        return Bishop(name, point, image)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
