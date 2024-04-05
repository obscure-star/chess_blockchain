---
title: Pawn data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/Pawn.kt" line="8">

---

This code snippet defines a `Pawn` class that represents a pawn piece in a chess game. It implements the `PieceType` interface and overrides its methods. The `movePattern` function calculates and returns a set of valid positions that the pawn can move to based on its current position and the positions of other pieces on the board. The function checks for valid moves in different directions, such as moving forward, capturing diagonally, and making a double move on the pawn's initial position.

```kotlin
data class Pawn(
    override val name: String = "pawn",
    override val point: Int = 1,
    override val image: String = " P ",
) : PieceType {
    override fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
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
                    } else if (abs(colOffset) != abs(rowOffset) && !otherPlayerPiecePositions.contains("${newCol.toColumn()}$newRow")) {
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

    override fun copy(): Pawn {
        return Pawn(name, point, image)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
