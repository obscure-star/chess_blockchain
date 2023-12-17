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
10     fun main() {
11         fancyPrintln("Welcome to the best game of Chess!")
12         do {
13             fancyPrintln("Input a player (white/black): ")
14             val inputPlayer = readlnOrNull()
15             if (checkExitGame(inputPlayer)) {
16                 fancyPrintln("exiting game :(")
17                 return
18             }
19             val correctInput = inputPlayer?.matches(Regex("^(white|black)$")) == true
20             if (correctInput) {
21                 fancyPrintln("You will be playing as $inputPlayer")
22                 startGame(inputPlayer!!)
23             } else {
24                 fancyPrintln("You didn't enter white or black. You entered: $inputPlayer. Please enter (white/black).")
25             }
26         } while (!correctInput)
27     }
```

<br/>

This code snippet defines a function called `startGame` that takes a `player` parameter. The main functionality of the code is to start a new game based on the chosen player. If the `player` is "white", it starts a new game with a `WhitePlayer` and a `BlackPlayer`. If the `player` is not "white", it starts a new game with a `BlackPlayer` and a `WhitePlayer`.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/App.kt
```kotlin
29     fun startGame(player: String) {
30         if (player == "white") {
31             Game.startNewGame(
32                 pickedPlayer =
33                     WhitePlayer(
34                         name = "white",
35                         playerPoints = 0,
36                     ),
37                 otherPlayer =
38                     BlackPlayer(
39                         name = "black",
40                         playerPoints = 0,
41                     ),
42             )
43         } else {
44             Game.startNewGame(
45                 pickedPlayer =
46                     BlackPlayer(
47                         name = "black",
48                         playerPoints = 0,
49                     ),
50                 otherPlayer =
51                     WhitePlayer(
52                         name = "white",
53                         playerPoints = 0,
54                     ),
55             )
56         }
57     }
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
34         private fun start() {
35             while (true) {
36                 currentPlayer.saveState()
37                 otherPlayer.saveState()
38                 var castleMove: String?
39                 fancyPrintln("${currentPlayer.name} has ${currentPlayer.playerPoints} points")
40                 fancyPrintln("${otherPlayer.name} has ${otherPlayer.playerPoints} points")
41     
42                 // check if current king is checked
43                 val isCheck = isCheck()
44                 if (isCheck) {
45                     updateAllOpenMoves(otherPlayer, currentPlayer)
46                     if (isCheckMate()) {
47                         fancyPrintln("Checkmate! ${otherPlayer.name} has won with ${otherPlayer.playerPoints} points.")
48                         otherPlayer.setWinner()
49                         return
50                     }
51                 }
52     
53                 // enter move
54                 fancyPrintln("Please enter your move (example: e2-e4): ")
55                 val move = readlnOrNull()
56                 if (checkExitGame(move)) {
57                     fancyPrintln("exiting game :(")
58                     return
59                 }
60                 val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
61                 if (isCorrectInput) {
62                     if (!checkMove(move!!)) continue
63                     castleMove = getCastleMove(move, isCheck)
64                 } else {
65                     fancyPrintln("Please enter a valid move like (e2-e4)")
66                     continue
67                 }
68     
69                 // promote pawn if needed
70                 currentPlayer.selectedPiece?.let { selectedPiece ->
71                     currentPlayer.destinationPiece?.let { destinationPiece ->
72                         processPromotePawn(selectedPiece, destinationPiece)
73                     }
74                 }
75     
76                 fancyPrintln("${currentPlayer.selectedPiece?.name} open moves: ${currentPlayer.selectedPiece?.openMoves}")
77     
78                 // update pieces and players
79                 updatePlayerPieces()
80                 updateScores()
81                 updateDestinationPiece()
82     
83                 // if action leads to check restore player states
84                 if (currentPlayer.selectedPiece?.let { leadsToCheck(it, checkPieceMoves = false) } == true) {
85                     currentPlayer.restoreState()
86                     otherPlayer.restoreState()
87                     continue
88                 }
89     
90                 fancyPrintln(
91                     "${currentPlayer.selectedPiece?.name} ${currentPlayer.destinationPiece?.position} " +
92                         "to ${currentPlayer.selectedPiece?.position} has been played.",
93                 )
94     
95                 updateBoard()
96     
97                 // check if move is a castle move
98                 if (castleMove in CASTLE_MOVES_MAP) {
99                     if (castleMove != null) {
100                        swapRook(castleMove)
101                    }
102                }
103    
104                board.printBoard()
105    
106                // update open moves based on currentPlayer's open moves
107                updateAllOpenMoves(currentPlayer, otherPlayer)
108    
109                // switch current player
110                if (currentPlayer.name == "white") {
111                    currentPlayer = secondPlayer
112                    otherPlayer = firstPlayer
113                } else {
114                    currentPlayer = firstPlayer
115                    otherPlayer = secondPlayer
116                }
117                fancyPrintln("${currentPlayer.name}'s turn. Press q to quit")
118            }
119        }
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

<br/>

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

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/xzoctckr).
