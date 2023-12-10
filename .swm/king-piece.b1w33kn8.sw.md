---
id: b1w33kn8
title: King Piece
file_version: 1.1.3
app_version: 1.18.32
---

This code snippet defines a `King` data class that represents a king piece in a chess game. The `King` class implements the `PieceType` interface and overrides its `movePattern` function. The `movePattern` function calculates the valid positions for the king to move to based on its current position, the positions of the player's pieces, the positions of the opponent's pieces, and the positions of all open pieces. The function checks for valid positions by considering the king's castle rook positions and verifying if there are no pieces between the king's current position and the new position. The valid positions are added to a set and returned by the function.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/pieceTypes/King.kt
```kotlin
8      data class King(
9          override val name: String = "king",
10         override val point: Int? = null,
11         override val image: String = " K ",
12         val castleRookPositions: MutableList<Position> = mutableListOf(),
13     ) : PieceType {
14         override fun movePattern(
15             position: Position,
16             playerPiecePositions: List<String>,
17             otherPlayerPiecePositions: List<String>,
18             otherPlayerAllOpenMoves: List<Position>,
19         ): Set<Position> {
20             val validPositions = mutableSetOf<Position>()
21     
22             fun checkInBetweenPieces(
23                 newKingPosition: Position,
24                 columnOffsets: List<Int>,
25             ): Boolean {
26                 for (offset in columnOffsets) {
27                     val newKingPositionOffset = Position(newKingPosition.row, (newKingPosition.column.toColumnNumber() + offset + 1).toColumn())
28                     if (!otherPlayerPiecePositions.contains(newKingPositionOffset.toString()) &&
29                         !playerPiecePositions.contains(newKingPositionOffset.toString())
30                     ) {
31                         // Continue to the next iteration
32                         continue
33                     } else {
34                         // Set the variable to false and break out of the loop
35                         return false
36                     }
37                 }
38                 return true
39             }
40     
41             // add castle moves
42             validPositions.addAll(
43                 castleRookPositions
44                     .map { rookPosition ->
45                         Position(
46                             position.row,
47                             (
48                                 ceil(
49                                     (
50                                         position.column.toColumnNumber() +
51                                             rookPosition.column.toColumnNumber()
52                                     ).toDouble() / 2,
53                                 ) + 1
54                             ).toInt().toColumn(),
55                         )
56                     }
57                     .filter { newKingPosition ->
58                         val isValid =
59                             if (newKingPosition.column == "c") {
60                                 checkInBetweenPieces(newKingPosition, listOf(-1, 1))
61                             } else {
62                                 checkInBetweenPieces(newKingPosition, listOf(-1))
63                             }
64                         // Return the value of the boolean variable
65                         isValid
66                     },
67             )
68     
69             fun addIfValid(
70                 colOffset: Int,
71                 rowOffset: Int,
72             ) {
73                 val newCol = position.column.toColumnNumber() + colOffset + 1
74                 val newRow = position.row + rowOffset
75     
76                 if (newCol in 1..8 && newRow in 1..8) {
77                     val newPosition = Position(newRow, newCol.toColumn())
78                     if (!playerPiecePositions.contains(newPosition.toString()) &&
79                         otherPlayerAllOpenMoves.none { it.toString() == newPosition.toString() }
80                     ) {
81                         validPositions.add(newPosition)
82                     }
83                 }
84             }
85     
86             // Horizontal and Vertical
87             for (offset in -1..1) {
88                 for (innerOffset in -1..1) {
89                     if (offset != 0 || innerOffset != 0) {
90                         addIfValid(offset, innerOffset)
91                     }
92                 }
93             }
94     
95             // Diagonals
96             for (colOffset in -1..1) {
97                 for (rowOffset in -1..1) {
98                     if (colOffset != 0 || rowOffset != 0) {
99                         addIfValid(colOffset, rowOffset)
100                    }
101                }
102            }
103    
104            // fancyPrintln("These are the valid positions: $validPositions")
105            return validPositions
106        }
107    }
```

<br/>

<br/>

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/b1w33kn8).
