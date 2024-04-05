---
title: Black pawn data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/BlackPawn.kt" line="8">

---

This code snippet defines a data class <SwmToken path="/app/src/main/kotlin/chess/common/model/pieceTypes/BlackPawn.kt" pos="8:4:4" line-data="data class BlackPawn(">`BlackPawn`</SwmToken> that represents a black pawn piece in a chess game. It implements the <SwmToken path="/app/src/main/kotlin/chess/common/model/pieceTypes/BlackPawn.kt" pos="12:4:4" line-data=") : PieceType {">`PieceType`</SwmToken> interface and overrides its functions. The <SwmToken path="/app/src/main/kotlin/chess/common/model/pieceTypes/BlackPawn.kt" pos="13:5:5" line-data="    override fun movePattern(">`movePattern`</SwmToken> function calculates the valid positions that the black pawn can move to based on its current position and the positions of other pieces on the board. It checks for standard pawn moves, double moves on the pawn's initial position, and captures diagonally. The valid positions are returned as a set of <SwmToken path="/app/src/main/kotlin/chess/common/model/pieceTypes/BlackPawn.kt" pos="14:1:1" line-data="        position: Position,">`position`</SwmToken> objects.

```kotlin
data class BlackPawn(
    override val name: String = "black_pawn",
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
        addIfValid(0, -1)

        // Pawn's initial double move
        if (position.row == 7) {
            addIfValid(0, -2)
        }

        // Pawn captures diagonally
        addIfValid(-1, -1)
        addIfValid(1, -1)

        // fancyPrintln("These are the valid positions: $validPositions")
        return validPositions
    }

    override fun copy(): BlackPawn {
        return BlackPawn(name, point, image)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
