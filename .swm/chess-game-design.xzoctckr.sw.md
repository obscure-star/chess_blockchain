---
title: Chess game design
---
The game flow:

<SwmSnippet path="/app/src/main/kotlin/chess/App.kt" line="10">

---

This code snippet prompts the user to input a <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="436:4:4" line-data="            pickedPlayer: Player,">`Player`</SwmToken> color <SwmToken path="/app/src/main/kotlin/chess/App.kt" pos="13:10:14" line-data="        fancyPrintln(&quot;Input a player (white/black): &quot;)">`(white/black)`</SwmToken> for a game of Chess. It checks if the input is valid and continues to prompt the user until a valid input is provided. Once a valid input is received, the game starts with the chosen <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="436:4:4" line-data="            pickedPlayer: Player,">`Player`</SwmToken> color.

```kotlin
fun main() {
    fancyPrintln("Welcome to the best game of Chess!")
    do {
        fancyPrintln("Input a player (white/black): ")
        val inputPlayer = readlnOrNull()

        if (checkExitGame(inputPlayer)) {
            fancyPrintln("Exiting game :(")
            return
        }

        val correctInput = inputPlayer?.matches(Regex("^(white|black)$", RegexOption.IGNORE_CASE)) == true

        if (correctInput) {
            fancyPrintln("You will be playing as $inputPlayer")
            startGame(inputPlayer!!)
        } else {
            val databaseFlag = inputPlayer?.matches(Regex("^(white|black)\\sd\\b", RegexOption.IGNORE_CASE))
            val aiFlag = inputPlayer?.matches(Regex("^(white|black)\\sa\\b", RegexOption.IGNORE_CASE))
            val sanitizedInput = inputPlayer?.replace(Regex("^(white|black)\\s[da]\\b", RegexOption.IGNORE_CASE), "$1") ?: ""

            if (sanitizedInput.isNotEmpty() && sanitizedInput == "white" || sanitizedInput == "black") {
                fancyPrintln("You will be playing as $sanitizedInput")
                if (databaseFlag == true) startGame(sanitizedInput, withDatabaseConnection = true)
                if (aiFlag == true) startGame(sanitizedInput, withDatabaseConnection = true, withAi = true)
            } else {
                fancyPrintln("Invalid input. Please enter (white/black).")
            }
        }
    } while (!correctInput)
}
```

---

</SwmSnippet>

<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Game.kt" line="435">

---

This code snippet defines a function called `startGame` that takes a `player` parameter. The main functionality of the code is to start a new game based on the chosen player. If the `player` is "white", it starts a new game with a `WhitePlayer` and a `BlackPlayer`. If the `player` is not "white", it starts a new game with a `BlackPlayer` and a `WhitePlayer`.

```kotlin
        fun startNewGame(
            pickedPlayer: Player,
            otherPlayer: Player,
            withDatabaseConnection: Boolean = false,
            withAi: Boolean = false,
        ) {
            currentGame = Game(pickedPlayer, otherPlayer)
            if (withDatabaseConnection) {
                connection =
                    runBlocking {
                        try {
                            SQLConnection.connection()
                        } catch (e: Exception) {
                            // Handle exceptions, log, or return null as appropriate
                            null
                        }
                    }
                chessDataDAO = connection?.let { ChessDataDAO(it) }
                if (withAi) {
                    neuralNetworkImplementation = connection?.let { NeuralNetworkImplementation(it) }
                }
            }
            currentGame?.start(withDatabaseConnection, withAi)
        }
```

---

</SwmSnippet>

<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Game.kt" line="34">

---

This code initializes the game by setting the <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="36:1:1" line-data="            currentPlayer = firstPlayer">`currentPlayer`</SwmToken> and <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="37:1:1" line-data="            otherPlayer = secondPlayer">`otherPlayer`</SwmToken> based on the <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="35:6:6" line-data="        if (firstPlayer.name == &quot;white&quot;) {">`name`</SwmToken> property of the <SwmToken path="/app/src/main/kotlin/chess/common/model/Game.kt" pos="35:4:4" line-data="        if (firstPlayer.name == &quot;white&quot;) {">`firstPlayer`</SwmToken>. It then builds the game board and sets the pieces for both players.

```kotlin
    init {
        if (firstPlayer.name == "white") {
            currentPlayer = firstPlayer
            otherPlayer = secondPlayer
        } else {
            currentPlayer = secondPlayer
            otherPlayer = firstPlayer
        }
        board.buildBoard()
        firstPlayer.setOwnPieces()
        secondPlayer.setOwnPieces()
    }
```

---

</SwmSnippet>

[UML diagram](https://lucid.app/lucidchart/f3380b51-5d2b-4a2b-9488-b141513f322d/edit?beaconFlowId=2BFFD1101C2A7305&invitationId=inv_79cb4f25-8b57-4510-b5e2-d1734be0a63b&page=0_0#) for the classes:

<p align="center"><img src="https://firebasestorage.googleapis.com/v0/b/swimmio.appspot.com/o/repositories%2FZ2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI%3D%2F7602b0d0-6939-4fcd-afd2-ce00fad54ea3.png?alt=media&amp;token=436f0cd8-756f-47b5-86f0-c03d6b5c23da" style="width: 100%"></p>

[Flow diagram](https://lucid.app/lucidchart/59d777de-515f-48e4-a5b4-6c1e91130f47/edit?beaconFlowId=8BF8868651B78A79&invitationId=inv_eebfcf41-b2af-4b0b-8c7c-56a0997f909c&page=0_0#) for the chess game flow:

Castle moves map:

This map is used to check if the move is a castle move and use the value as the location address and the destination address for the rook

<SwmSnippet path="/app/src/main/kotlin/chess/common/model/Game.kt" line="464">

---

&nbsp;

```kotlin
        val CASTLE_MOVES_MAP =
            mapOf(
                "e1-c1" to Pair("a1", "d1"),
                "e1-g1" to Pair("h1", "f1"),
                "e8-c8" to Pair("a8", "d8"),
                "e8-g8" to Pair("h8", "f8"),
            )
```

---

</SwmSnippet>

Example: "e1-c1" is the move entered "a1" is the location address for the rook and "d1" is the destination address for the rook

## Machine learning implementation

To implement machine learning it is essential that the player moves are persisted. This would involve:

- Creating a database to persist each move's data

- The database can look like this:

| Round | Board representation<br><br>(one hot encoding)                     | Piece count                                                                                                    | Legal moves          | Threats and attacks          | Piece activity<br><br>(how active each piece will contribute to game)                                          | King Safety<br><br>(solid pawn structure (shelter), castling rights, open lines of attack) | Pawn structure<br><br>(pawn chains, isolated, double)                                                                                                       | Material Balance       | Center Control<br><br>(dependent on center locations chess.D4, chess.E4, chess.D5, chess.E5) | Previous moves        |
| ----- | ------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------- | -------------------- | ---------------------------- | -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- | -------------------------------------------------------------------------------------------- | --------------------- |
| 1     | 0b1111111111111111000000000000000000000000000000001111111111111111 | { 'bP': 0, 'bN': 0, 'bB': 0, 'bR': 0, 'bQ': 0, 'bK': 0, 'wP': 0, 'wN': 0, 'wB': 0, 'wR': 0, 'wQ': 0, 'wK': 0 } | {"e2-e4", "d2-d4...} | {"e2", "e4", "f2", "e8",...} | { 'bP': 0, 'bN': 0, 'bB': 0, 'bR': 0, 'bQ': 0, 'bK': 0, 'wP': 0, 'wN': 0, 'wB': 0, 'wR': 0, 'wQ': 0, 'wK': 0 } | {bK: 3, wK: 3}                                                                             | {extracted_pawns: {"e2", "e4", "e5"...},<br><br>pawn_chains: {"e5", "e6"},<br><br>doubled_pawns: {"f3", "d2"},<br><br>backward_pawns: {"e5", "g7"}<br><br>} | {white: 39, black: 39} | {white: 3, black: 0}                                                                         | {"e2-e4", "f2-f4"...} |
| ...   | &nbsp;                                                             | &nbsp;                                                                                                         | &nbsp;               | &nbsp;                       | &nbsp;                                                                                                         | &nbsp;                                                                                     | &nbsp;                                                                                                                                                      | &nbsp;                 | &nbsp;                                                                                       | &nbsp;                |

- The model will use data from the table to determine best moves

- The tool to handle database persistence will depend on:

  - Database type

    - SQL - Allows for complex queries and handles structured data

    - NoSQL - Allows for scalability

  - Scalability

    - AI model needs to be scalable so this

  - Data integrity and transactions

  - Query language

    - Query language will primarily be SQL

  - Consistency and Support

  - Security

I wanted to keep the database simple and iterate on it so I'm going to use what is most familiar to me which is SQL. I will be using MySQL as my Database Management tool

For setting up the table a data class is needed to store all the computation that is done during a round in the game

<SwmSnippet path="/app/src/main/kotlin/chess/database/ChessData.kt" line="6">

---

&nbsp;

```kotlin
data class ChessData(
    val gameId: String,
    val round: Int,
    val boardRepresentation: String,
    val boardRepresentationInt: Int,
    val pieceCount: String,
    val legalMoves: MutableMap<String, MutableList<String>>,
    val threatsAndAttacks: String,
    val pieceActivity: String,
    val kingSafety: String,
    val pawnStructure: String?,
    val materialBalance: String,
    val centerControl: String,
    val previousMoves: String,
    val move: String,
    val blackWin: Boolean,
    val whiteWin: Boolean,
    val winner: Int,
    var nextMove: String? = "END",
    var nextMoveIndex: Int = -1,
    var lengthLegalMoves: Int = -1,
)
```

---

</SwmSnippet>

This data class is populated and is used to process data that should be sent to the chess_data database

<SwmSnippet path="/app/src/main/kotlin/chess/database/ChessDataDAO.kt" line="15">

---

&nbsp;

```kotlin
                INSERT INTO CHESS_DATA (
                    GAME_ID,
                    ROUND,
                    BOARD_REPRESENTATION,
                    BOARD_REPRESENTATION_INT,
                    PIECE_COUNT,
                    THREATS_AND_ATTACKS,
                    PIECE_ACTIVITY,
                    KING_SAFETY,
                    PAWN_STRUCTURE,
                    MATERIAL_BALANCE,
                    CENTER_CONTROL,
                    PREVIOUS_MOVES,
                    MOVE,
                    BLACK_WINS,
                    WHITE_WINS,
                    WINNER,
                    NEXT_MOVE,
                    NEXT_MOVE_INDEX,
                    LENGTH_LEGAL_MOVES,
                    LEGAL_MOVES
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
```

---

</SwmSnippet>

To prevent Data truncation: Invalid JSON text: "Missing a name for object member." at position 1 in value for column 'CHESS_DATA.PIECE_COUNT' errors the String value should be converted to json format using the Json.encodeToString() function:

<SwmSnippet path="/app/src/main/kotlin/chess/database/ChessDataDAO.kt" line="45">

---

&nbsp;

```kotlin
            preparedStatement.setString(5, Json.encodeToString(chessData.pieceCount))
```

---

</SwmSnippet>

To run the Database test suite you can use the command

```
./gradlew test --tests chess.DatabaseTest
```

<SwmMeta version="3.0.0" repo-id="Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=" repo-name="Chess"><sup>Powered by [Swimm](https://app.swimm.io/)</sup></SwmMeta>
