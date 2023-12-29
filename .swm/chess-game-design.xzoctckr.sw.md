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
386            fun startNewGame(
387                pickedPlayer: Player,
388                otherPlayer: Player,
389                withDatabaseConnection: Boolean = false,
390            ) {
391                currentGame = Game(pickedPlayer, otherPlayer)
392                if (withDatabaseConnection) {
393                    connection =
394                        runBlocking {
395                            try {
396                                SQLConnection.connection()
397                            } catch (e: Exception) {
398                                // Handle exceptions, log, or return null as appropriate
399                                null
400                            }
401                        }
402                    chessDataDAO = connection?.let { ChessDataDAO(it) }
403                }
404                currentGame?.start(withDatabaseConnection)
405            }
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

For setting up the table a data class is needed to store all the computation that is done during a round in the game

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/ChessData.kt
```kotlin
6      data class ChessData(
7          val gameId: String,
8          val round: Int,
9          val boardRepresentation: String,
10         val boardRepresentationInt: Int,
11         val pieceCount: String,
12         val legalMoves: MutableMap<String, MutableList<String>>,
13         val threatsAndAttacks: String,
14         val pieceActivity: String,
15         val kingSafety: String,
16         val pawnStructure: String?,
17         val materialBalance: String,
18         val centerControl: String,
19         val previousMoves: String,
20         val move: String,
21         val blackWin: Boolean,
22         val whiteWin: Boolean,
23         val winner: Int,
24         var nextMove: String? = "END",
25         var nextMoveIndex: Int = -1,
26         var lengthLegalMoves: Int = -1,
27     )
```

<br/>

This data class is populated and is used to process data that should be sent to the chess\_data database

<br/>


<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/database/ChessDataDAO.kt
```kotlin
15                     INSERT INTO CHESS_DATA (
16                         GAME_ID,
17                         ROUND,
18                         BOARD_REPRESENTATION,
19                         BOARD_REPRESENTATION_INT,
20                         PIECE_COUNT,
21                         THREATS_AND_ATTACKS,
22                         PIECE_ACTIVITY,
23                         KING_SAFETY,
24                         PAWN_STRUCTURE,
25                         MATERIAL_BALANCE,
26                         CENTER_CONTROL,
27                         PREVIOUS_MOVES,
28                         MOVE,
29                         BLACK_WINS,
30                         WHITE_WINS,
31                         WINNER,
32                         NEXT_MOVE,
33                         NEXT_MOVE_INDEX,
34                         LENGTH_LEGAL_MOVES,
35                         LEGAL_MOVES
36                     ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
37                     """.trimIndent()
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

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/xzoctckr).
