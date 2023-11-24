import common.model.Board
import common.model.Game
import common.model.players.BlackPlayer
import common.model.players.WhitePlayer

fun main(args: Array<String>) {
    fancyPrintln("Welcome to the best game of Chess!")
    do {
        fancyPrintln("Input a player (white/black): ")
        val player = readlnOrNull()
        val correctInput = player?.matches(Regex("^(white|black)$")) == true
        if (correctInput) {
            fancyPrintln("You will be playing as $player")
            startGame(player!!)
        } else {
            fancyPrintln("You didn't enter white or black. You entered: $player. Please enter (white/black).")
        }
    } while (!correctInput)
}

fun startGame(player: String){
    var game: Game
    if (player == "white") {
        game = Game(
            pickedPlayer = WhitePlayer(
                name = "white",
                points = 0L
            ),
            otherPlayer = BlackPlayer(
                name = "black",
                points = 0L
            ),
        )
    } else {
        game = Game(
            pickedPlayer = BlackPlayer(
                name = "black",
                points = 0L
            ),
            otherPlayer = WhitePlayer(
                name = "white",
                points = 0L
            ),
        )
    }
    game.start()
}