---
id: xzoctckr
title: Chess game design
file_version: 1.1.3
app_version: 1.18.32
---

The game flow:

<br/>

This code snippet prompts the user to input a player color (white/black) for a game of Chess. It checks if the input is valid and continues to prompt the user until a valid input is provided. Once a valid input is received, the game starts with the chosen player color.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/App.kt
```kotlin
12     fun main() {
13         fancyPrintln("Welcome to the best game of Chess!")
14         do {
15             fancyPrintln("Input a player (white/black): ")
16             val inputPlayer = readlnOrNull()
17     
18             if (checkExitGame(inputPlayer)) {
19                 fancyPrintln("Exiting game :(")
20                 return
21             }
22     
23             val correctInput = inputPlayer?.matches(Regex("^(white|black)$", RegexOption.IGNORE_CASE)) == true
24     
25             if (correctInput) {
26                 fancyPrintln("You will be playing as $inputPlayer")
27                 startGame(inputPlayer!!)
28             } else {
29                 val sanitizedInput = inputPlayer?.replace(Regex("^(white|black)\\sd\\b", RegexOption.IGNORE_CASE), "$1") ?: ""
30     
31                 if (sanitizedInput.isNotEmpty()) {
32                     fancyPrintln("You will be playing as $sanitizedInput")
33                     startGame(sanitizedInput, true)
34                 } else {
35                     fancyPrintln("Invalid input. Please enter (white/black).")
36                 }
37             }
38         } while (!correctInput)
39     }
```

<br/>

This code snippet defines a function called `startGame` that takes a `player` parameter. The main functionality of the code is to start a new game based on the chosen player. If the `player` is "white", it starts a new game with a `WhitePlayer` and a `BlackPlayer`. If the `player` is not "white", it starts a new game with a `BlackPlayer` and a `WhitePlayer`.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/Game.kt
```kotlin
356            fun startNewGame(
357                pickedPlayer: Player,
358                otherPlayer: Player,
359                withDatabaseConnection: Boolean = false,
360            ) {
361                currentGame = Game(pickedPlayer, otherPlayer)
362                if (withDatabaseConnection) {
363                    connection =
364                        runBlocking {
365                            try {
366                                SQLConnection.connection()
367                            } catch (e: Exception) {
368                                // Handle exceptions, log, or return null as appropriate
369                                null
370                            }
371                        }
372                }
373                currentGame?.start(withDatabaseConnection)
374            }
```

<br/>

This code initializes the game by setting the `currentPlayer` and `otherPlayer` based on the `name` property of the `firstPlayer`. It then builds the game board and sets the pieces for both players.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/Game.kt
```kotlin
21         init {
22             if (firstPlayer.name == "white") {
23                 currentPlayer = firstPlayer
24                 otherPlayer = secondPlayer
25             } else {
26                 currentPlayer = secondPlayer
27                 otherPlayer = firstPlayer
28             }
29             board.buildBoard()
30             firstPlayer.setOwnPieces()
31             secondPlayer.setOwnPieces()
32         }
```

<br/>

This code snippet is part of a game logic implementation. It handles the main functionality of the game, including saving player states, checking for checkmate, getting user moves, validating moves, processing pawn promotion, updating player and piece states, and checking if an action leads to check.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/Game.kt
```kotlin
42         private fun start(withDatabaseConnection: Boolean = false) {
43             while (true) {
44                 currentPlayer.saveState()
45                 otherPlayer.saveState()
46                 var castleMove: String?
47                 fancyPrintln("${currentPlayer.name} has ${currentPlayer.playerPoints} points")
48                 fancyPrintln("${otherPlayer.name} has ${otherPlayer.playerPoints} points")
49     
50                 // check if current king is checked
51                 val isCheck = isCheck()
52                 if (isCheck) {
53                     updateAllOpenMoves(otherPlayer, currentPlayer)
54                     if (isCheckMate()) {
55                         fancyPrintln("Checkmate! ${otherPlayer.name} has won with ${otherPlayer.playerPoints} points.")
56                         otherPlayer.setWinner()
57                         round += 1
58                         sendToDatabase(withDatabaseConnection)
59                         return
60                     }
61                 }
62     
63                 // enter move
64                 fancyPrintln("Please enter your move (example: e2-e4): ")
65                 val move = readlnOrNull()
66                 if (checkExitGame(move)) {
67                     fancyPrintln("exiting game :(")
68                     return
69                 }
70                 val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
71                 if (isCorrectInput) {
72                     if (!checkMove(move!!)) continue
73                     castleMove = getCastleMove(move, isCheck)
74                 } else {
75                     fancyPrintln("Please enter a valid move like (e2-e4)")
76                     continue
77                 }
78     
79                 // promote pawn if needed
80                 currentPlayer.selectedPiece?.let { selectedPiece ->
81                     currentPlayer.destinationPiece?.let { destinationPiece ->
82                         processPromotePawn(selectedPiece, destinationPiece)
83                     }
84                 }
85     
86                 fancyPrintln("${currentPlayer.selectedPiece?.name} open moves: ${currentPlayer.selectedPiece?.openMoves}")
87     
88                 // update pieces and players
89                 updatePlayerPieces()
90                 updateScores()
91                 updateDestinationPiece()
92     
93                 // if action leads to check restore player states
94                 if (currentPlayer.selectedPiece?.let { leadsToCheck(it, checkPieceMoves = false) } == true) {
95                     currentPlayer.restoreState()
96                     otherPlayer.restoreState()
97                     continue
98                 }
99     
100                fancyPrintln(
101                    "${currentPlayer.selectedPiece?.name} ${currentPlayer.destinationPiece?.position} " +
102                        "to ${currentPlayer.selectedPiece?.position} has been played.",
103                )
104    
105                // update previous moves
106                previousMoves.add(move)
107                updateBoard()
108    
109                // check if move is a castle move
110                if (castleMove in CASTLE_MOVES_MAP) {
111                    if (castleMove != null) {
112                        swapRook(castleMove)
113                    }
114                }
115    
116                board.printBoard()
117    
118                // update open moves based on currentPlayer's open moves
119                updateAllOpenMoves(currentPlayer, otherPlayer)
120    
121                round += 1
122    
123                sendToDatabase(withDatabaseConnection)
124    
125                // switch current player
126                if (currentPlayer.name == "white") {
127                    currentPlayer = secondPlayer
128                    otherPlayer = firstPlayer
129                } else {
130                    currentPlayer = firstPlayer
131                    otherPlayer = secondPlayer
132                }
133                fancyPrintln("${currentPlayer.name}'s turn. Press q to quit")
134            }
135        }
```

<br/>

[UML diagram](https://lucid.app/lucidchart/f3380b51-5d2b-4a2b-9488-b141513f322d/edit?beaconFlowId=2BFFD1101C2A7305&invitationId=inv_79cb4f25-8b57-4510-b5e2-d1734be0a63b&page=0_0#) for the classes:

<br/>

<div align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2F7602b0d0-6939-4fcd-afd2-ce00fad54ea3.png?alt=media&token=436f0cd8-756f-47b5-86f0-c03d6b5c23da" style="width:'100%'"/></div>

<br/>

[Flow diagram](https://lucid.app/lucidchart/59d777de-515f-48e4-a5b4-6c1e91130f47/edit?beaconFlowId=8BF8868651B78A79&invitationId=inv_eebfcf41-b2af-4b0b-8c7c-56a0997f909c&page=0_0#) for the chess game flow:

Castle moves map:

This map is used to check if the move is a castle move and use the value as the location address and the destination address for the rook

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/Game.kt
```kotlin
296            val CASTLE_MOVES_MAP =
297                mapOf(
298                    "e1-c1" to Pair("a1", "d1"),
299                    "e1-g1" to Pair("h1", "f1"),
300                    "e8-c8" to Pair("a8", "d8"),
301                    "e8-g8" to Pair("h8", "f8"),
302                )
```

<br/>

Example: "e1-c1" is the move entered "a1" is the location address for the rook and "d1" is the destination address for the rook

## Machine learning implementation

To implement machine learning it is essential that the player moves are persisted. This would involve:

*   Creating a database to persist each move's data

*   The database can look like this:

<br/>

|Round|Board representation<br/><br>(one hot encoding)                   |Piece count                                                                                                   |Legal moves         |Threats and attacks         |Piece activity<br/><br>(how active each piece will contribute to game)                                        |King Safety<br/><br>(solid pawn structure (shelter), castling rights, open lines of attack)|Pawn structure<br/><br>(pawn chains, isolated, double)                                                                                                         |Material Balance      |Center Control<br><br>(dependent on center locations chess.D4, chess.E4, chess.D5, chess.E5)|Previous moves       |
|-----|------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|--------------------|----------------------------|--------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------|--------------------------------------------------------------------------------------------|---------------------|
|1    |0b1111111111111111000000000000000000000000000000001111111111111111|{ 'bP': 0, 'bN': 0, 'bB': 0, 'bR': 0, 'bQ': 0, 'bK': 0, 'wP': 0, 'wN': 0, 'wB': 0, 'wR': 0, 'wQ': 0, 'wK': 0 }|{"e2-e4", "d2-d4...}|{"e2", "e4", "f2", "e8",...}|{ 'bP': 0, 'bN': 0, 'bB': 0, 'bR': 0, 'bQ': 0, 'bK': 0, 'wP': 0, 'wN': 0, 'wB': 0, 'wR': 0, 'wQ': 0, 'wK': 0 }|{bK: 3, wK: 3}                                                                             |{extracted\_pawns: {"e2", "e4", "e5"...},<br><br>pawn\_chains: {"e5", "e6"},<br><br>doubled\_pawns: {"f3", "d2"},<br><br>backward\_pawns: {"e5", "g7"}<br><br>}|{white: 39, black: 39}|{white: 3, black: 0}                                                                        |{"e2-e4", "f2-f4"...}|
|...  |<br/>                                                             |<br/>                                                                                                         |<br/>               |<br/>                       |<br/>                                                                                                         |<br/>                                                                                      |<br/>                                                                                                                                                          |<br/>                 |<br/>                                                                                       |<br/>                |

<br/>

*   The model will use data from the table to determine best moves

*   The tool to handle database persistence will depend on:

    *   Database type

        *   SQL - Allows for complex queries and handles structured data

        *   NoSQL - Allows for scalability

    *   Scalability

        *   AI model needs to be scalable so this

    *   Data integrity and transactions

    *   Query language

        *   Query language will primarily be SQL

    *   Consistency and Support

    *   Security

I wanted to keep the database simple and iterate on it so I'm going to use what is most familiar to me which is SQL. I will be using MySQL as my Database Management tool

<br/>

For setting up the table a data class is needed to store all the computation that is done during a round in the game

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/ChessData.kt
```kotlin
6      data class ChessData(
7          val gameId: String,
8          val round: Int,
9          val boardRepresentation: String,
10         val pieceCount: String,
11         val legalMoves: String,
12         val threatsAndAttacks: String,
13         val pieceActivity: String,
14         val kingSafety: String,
15         val pawnStructure: String?,
16         val materialBalance: String,
17         val centerControl: String,
18         val previousMoves: String,
19         val blackWin: Boolean,
20         val whiteWin: Boolean,
21     )
```

<br/>

This data class is populated and is used to process data that should be sent to the chess\_data database

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/ChessDataDAO.kt
```kotlin
17                     INSERT INTO CHESS_DATA (
18                         ROUND_ID,
19                         GAME_ID,
20                         ROUND,
21                         BOARD_REPRESENTATION,
22                         PIECE_COUNT,
23                         LEGAL_MOVES,
24                         THREATS_AND_ATTACKS,
25                         PIECE_ACTIVITY,
26                         KING_SAFETY,
27                         PAWN_STRUCTURE,
28                         MATERIAL_BALANCE,
29                         CENTER_CONTROL,
30                         PREVIOUS_MOVES,
31                         BLACK_WINS,
32                         WHITE_WINS
33                     ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
```

<br/>

To prevent Data truncation: Invalid JSON text: "Missing a name for object member." at position 1 in value for column 'CHESS\_DATA.PIECE\_COUNT' errors the String value should be converted to json format using the Json.encodeToString() function:<br/>

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/ChessDataDAO.kt
```kotlin
42                 preparedStatement.setString(5, Json.encodeToString(chessData.pieceCount))
```

<br/>

To run the Database test suite you can use the command

```
./gradlew test --tests chess.DatabaseTest
```

<br/>

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/xzoctckr).
