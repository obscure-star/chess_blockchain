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
13         var canCastle: Boolean = false,
14     ) : PieceType {
15         override fun movePattern(
16             position: Position,
17             playerPiecePositions: List<String>,
18             otherPlayerPiecePositions: List<String>,
19             otherPlayerAllOpenMoves: List<Position>,
20         ): Set<Position> {
21             val validPositions = mutableSetOf<Position>()
22             canCastle = false
23     
24             fun checkInBetweenPieces(
25                 newKingPosition: Position,
26                 columnOffsets: List<Int>,
27             ): Boolean {
28                 for (offset in columnOffsets) {
29                     val newKingPositionOffset = Position(newKingPosition.row, (newKingPosition.column.toColumnNumber() + offset + 1).toColumn())
30                     if (!otherPlayerPiecePositions.contains(newKingPositionOffset.toString()) &&
31                         !playerPiecePositions.contains(newKingPositionOffset.toString())
32                     ) {
33                         // Continue to the next iteration
34                         continue
35                     } else {
36                         // Set the variable to false and break out of the loop
37                         return false
38                     }
39                 }
40                 return true
41             }
42     
43             // add castle moves
44             validPositions.addAll(
45                 castleRookPositions
46                     .map { rookPosition ->
47                         Position(
48                             position.row,
49                             (
50                                 ceil(
51                                     (
52                                         position.column.toColumnNumber() +
53                                             rookPosition.column.toColumnNumber()
54                                     ).toDouble() / 2,
55                                 ) + 1
56                             ).toInt().toColumn(),
57                         )
58                     }
59                     .filter { newKingPosition ->
60                         val isValid =
61                             if (newKingPosition.column == "c") {
62                                 checkInBetweenPieces(newKingPosition, listOf(-1, 1))
63                             } else {
64                                 checkInBetweenPieces(newKingPosition, listOf(-1))
65                             }
66                         // Return the value of the boolean variable
67                         isValid.also { if (it) canCastle = true }
68                     },
69             )
70     
71             fun addIfValid(
72                 colOffset: Int,
73                 rowOffset: Int,
74             ) {
75                 val newCol = position.column.toColumnNumber() + colOffset + 1
76                 val newRow = position.row + rowOffset
77     
78                 if (newCol in 1..8 && newRow in 1..8) {
79                     val newPosition = Position(newRow, newCol.toColumn())
80                     if (!playerPiecePositions.contains(newPosition.toString()) &&
81                         otherPlayerAllOpenMoves.none { it.toString() == newPosition.toString() }
82                     ) {
83                         validPositions.add(newPosition)
84                     }
85                 }
86             }
87     
88             // Horizontal and Vertical
89             for (offset in -1..1) {
90                 for (innerOffset in -1..1) {
91                     if (offset != 0 || innerOffset != 0) {
92                         addIfValid(offset, innerOffset)
93                     }
94                 }
95             }
96     
97             // Diagonals
98             for (colOffset in -1..1) {
99                 for (rowOffset in -1..1) {
100                    if (colOffset != 0 || rowOffset != 0) {
101                        addIfValid(colOffset, rowOffset)
102                    }
103                }
104            }
105    
106            // fancyPrintln("These are the valid positions: $validPositions")
107            return validPositions
108        }
109    
110        override fun copy(): King {
111            return King(name, point, image, castleRookPositions.map { it.copy() }.toMutableList(), canCastle)
112        }
```

<br/>

<br/>

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/b1w33kn8).
