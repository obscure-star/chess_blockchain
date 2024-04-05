---
title: Piece data class
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Piece.kt" line="7">

---

This code snippet defines a `Piece` data class that represents a game piece. It has properties such as `name`, `pieceType`, `initialPosition`, `position`, `openMoves`, and `color`. The class provides various functions to manipulate and update these properties, such as `setOpenMoves`, `getInstanceOpenMoves`, `clearOpenMoves`, `makeEmpty`, and `updatePosition`.

```kotlin
data class Piece(
    var name: String = "empty",
    var pieceType: PieceType = Empty(),
    var initialPosition: Position = Position(1, "a"),
    var position: Position = Position(1, "a"),
    var openMoves: Set<Position> = emptySet(),
    var color: String = Color.WHITE.code,
    var previousState: Piece? = null,
) {
    fun saveState() {
        previousState =
            Piece(
                name = name,
                pieceType = pieceType.copy(),
                initialPosition = initialPosition.copy(),
                position = position.copy(),
                openMoves = openMoves.map { it.copy() }.toSet(),
                color = color,
            )
    }

    fun restoreState() {
        name = previousState?.name.toString()
        pieceType = previousState?.pieceType ?: Empty()
        initialPosition = previousState?.initialPosition ?: Position(1, "a")
        position = previousState?.position ?: Position(1, "a")
        openMoves = previousState?.openMoves?.map { it.copy() }?.toSet() ?: emptySet()
        color = previousState?.color ?: Color.WHITE.code
    }

    fun setOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ) {
        openMoves =
            pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenMoves)
    }

    fun getInstanceOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): Set<Position> {
        return pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenPieces)
    }

    fun clearOpenMoves() {
        openMoves = emptySet()
    }

    fun makeEmpty() {
        name = "empty"
        pieceType = Empty()
        openMoves = emptySet()
        color = Color.WHITE.code
    }

    fun updatePosition(newPosition: Position) {
        position = newPosition
    }
}
```

---

</SwmSnippet>

<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Piece.kt" line="37">

---

This code snippet sets the `openMoves` variable by calling a `movePattern` function with certain arguments.

```kotlin
    fun setOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ) {
        openMoves =
            pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenMoves)
    }
```

---

</SwmSnippet>

<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Piece.kt" line="46">

---

This code snippet defines a function `getInstanceOpenMoves` which takes in several lists as arguments. It returns a set of positions based on a move pattern determined by the `pieceType`.

```kotlin
    fun getInstanceOpenMoves(
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenPieces: List<Position>,
    ): Set<Position> {
        return pieceType.movePattern(position, playerPiecePositions, otherPlayerPiecePositions, otherPlayerAllOpenPieces)
    }
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
