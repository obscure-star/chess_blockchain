/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class AppTest {
    private val originalIn = System.`in`
    private val originalOut = System.out

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `startGame function should create a game with the correct players`() {
        provideInput("q")
        // Run startGame with "white" player
        startGame("white")

        // Assert that a game with WhitePlayer and BlackPlayer is created
        // You need to have appropriate methods in the Game class to check the players
        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)
    }

    @Test
    fun `test for black bishop (b4) takes pawn (d2)`() {
        provideInput("white", "e2-e4", "e7-e6", "f1-b5", "f8-b4", "b5-d7", "b4-d2", "q")

        main()

        // Assert that a game with WhitePlayer and BlackPlayer is created
        // You need to have appropriate methods in the Game class to check the players
        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)
        val board = game?.board?.board
        if (board != null) {
            assertEquals(board[6][3].name, "black_bishop")
            assertEquals(board[1][4].name, "empty")
        }
    }

    // Helper method to provide input for testing
    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
