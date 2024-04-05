---
title: PieceType Interface
---
<SwmSnippet path="/app/src/main/kotlin/chess/common/model/pieceTypes/PieceType.kt" line="5">

---

This code defines an interface called `PieceType` that represents a type of game piece. The interface has properties such as `name`, `point`, and `image` to describe the piece. It also has a function called `movePattern` that determines the set of possible positions the piece can move to based on its current position, the positions of other player's pieces, and the positions of all open pieces.

```kotlin
interface PieceType {
    val name: String
    val point: Int?
    val image: String // can be an actual image

    fun movePattern(
        position: Position,
        playerPiecePositions: List<String>,
        otherPlayerPiecePositions: List<String>,
        otherPlayerAllOpenMoves: List<Position>,
    ): Set<Position>

    fun copy(): PieceType
}
```

---

</SwmSnippet>

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
