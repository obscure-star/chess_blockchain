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

This code snippet is a part of a game loop that keeps the game running until the player chooses to exit. It prompts the player for their move, validates the move, and handles various game conditions such as check and checkmate. It also updates the player's points, checks for valid input, and processes pawn promotion.
<!-- NOTE-swimm-snippet: the lines below link your snippet to Swimm -->
### 📄 app/src/main/kotlin/chess/common/model/Game.kt
```kotlin
34         fun start() {
35             // keep game running
36             do {
37                 val continueGame = playerAction()
38             } while (continueGame)
39         }
40     
41         private fun playerAction(): Boolean {
42             val currentPlayerState = currentPlayer.saveState()
43             val otherPlayerState = otherPlayer.saveState()
44             var castleMove: String? = null
45             do {
46                 fancyPrintln("${currentPlayer.name} has ${currentPlayer.playerPoints} points")
47                 fancyPrintln("${otherPlayer.name} has ${otherPlayer.playerPoints} points")
48                 val isCheck = isCheck()
49                 if (isCheck()) {
50                     updateAllOpenMoves(otherPlayer, currentPlayer)
51                     if (isCheckMate()) {
52                         fancyPrintln("Checkmate! ${otherPlayer.name} has won with ${otherPlayer.playerPoints} points.")
53                         otherPlayer.setWinner()
54                         return false
55                     }
56                 }
57                 fancyPrintln("Please enter your move (example: e2-e4): ")
58                 val move = readlnOrNull()
59                 if (checkExitGame(move)) {
60                     fancyPrintln("exiting game :(")
61                     return false
62                 }
63                 val isCorrectInput = move?.matches(Regex("[a-h][1-8]-[a-h][1-8]")) == true
64                 var isMoveValid = false
65                 if (isCorrectInput) {
66                     isMoveValid = checkMove(move!!)
67                     castleMove = getCastleMove(move, isCheck)
68                 } else {
69                     fancyPrintln("Please enter a valid move like (e2-e4)")
70                 }
71             } while (!isCorrectInput || !isMoveValid)
72             currentPlayer.selectedPiece?.let {
73                     selectedPiece ->
74                 currentPlayer.destinationPiece?.let {
75                         destinationPiece ->
76                     processPromotePawn(selectedPiece, destinationPiece)
77                 }
78             }
79             fancyPrintln("${currentPlayer.selectedPiece?.name} open moves: ${currentPlayer.selectedPiece?.openMoves}")
80             updatePlayerPieces()
81             updateScores()
82             updateDestinationPiece()
83             if (leadsToCheck()) {
84                 currentPlayer.restoreState(currentPlayerState)
85                 otherPlayer.restoreState(otherPlayerState)
86                 return true
87             }
88     
89             fancyPrintln(
90                 "${currentPlayer.selectedPiece?.name} ${currentPlayer.destinationPiece?.position} " +
91                     "to ${currentPlayer.selectedPiece?.position} has been played.",
92             )
93     
94             updateBoard()
95     
96             if (castleMove in CASTLE_MOVES_MAP) {
97                 if (castleMove != null) {
98                     swapRook(castleMove)
99                 }
100            }
101    
102            board.printBoard()
103    
104            // update open moves based on currentPlayer's open moves
105            updateAllOpenMoves(currentPlayer, otherPlayer)
106    
107            // switch current player
108            if (currentPlayer.name == "white") {
109                currentPlayer = secondPlayer
110                otherPlayer = firstPlayer
111            } else {
112                currentPlayer = firstPlayer
113                otherPlayer = secondPlayer
114            }
115            fancyPrintln("${currentPlayer.name}'s turn. Press q to quit")
116            return true
117        }
```

<br/>

This file was generated by Swimm. [Click here to view it in the app](https://app.swimm.io/repos/Z2l0aHViJTNBJTNBQ2hlc3MlM0ElM0FvYnNjdXJlLXN0YXI=/docs/xzoctckr).
