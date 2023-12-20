/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer

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
            val sanitizedInput = inputPlayer?.replace(Regex("^(white|black)\\sd\\b", RegexOption.IGNORE_CASE), "$1") ?: ""

            if (sanitizedInput.isNotEmpty()) {
                fancyPrintln("You will be playing as $sanitizedInput")
                startGame(sanitizedInput, true)
            } else {
                fancyPrintln("Invalid input. Please enter (white/black).")
            }
        }
    } while (!correctInput)
}

fun startGame(
    player: String,
    withDatabaseConnection: Boolean = false,
) {
    if (player == "white") {
        Game.startNewGame(
            pickedPlayer =
                WhitePlayer(
                    name = "white",
                    playerPoints = 0,
                ),
            otherPlayer =
                BlackPlayer(
                    name = "black",
                    playerPoints = 0,
                ),
            withDatabaseConnection,
        )
    } else {
        Game.startNewGame(
            pickedPlayer =
                BlackPlayer(
                    name = "black",
                    playerPoints = 0,
                ),
            otherPlayer =
                WhitePlayer(
                    name = "white",
                    playerPoints = 0,
                ),
            withDatabaseConnection,
        )
    }
}
