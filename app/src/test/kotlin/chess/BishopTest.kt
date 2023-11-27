package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class BishopTest {
    private val originalIn = System.`in`
    private val originalOut = System.out

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `test for white bishop (b4) takes black pawn (d2)`() {
        provideInput("white", "e2-e4", "e7-e6", "f1-b5", "f8-b4", "b5-d7", "b4-d2", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.firstPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.firstPlayer.destinationPiece!!.position

        assertEquals(
            "white_bishop",
            board[
                8 - pieceFinalPosition.row,
            ][pieceFinalPosition.column.toColumnNumber()].name,
        )
        assertEquals(
            "empty",
            board[8 - pieceInitialPosition.row][pieceInitialPosition.column.toColumnNumber()].name,
        )
    }

    @Test
    fun `test for black bishop (b4) can't go passed white pawn (d2)`() {
        provideInput("white", "e2-e4", "e7-e6", "f1-b5", "f8-b4", "b5-d7", "b4-e1", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_king",
            board[7]["e".toColumnNumber()].name,
        )
    }

    @Test
    fun `test for black bishop (b4) can't get to king (e1)`() {
        provideInput("white", "e2-e4", "e7-e6", "f1-b5", "f8-b4", "b5-d7", "e8-e7", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_king",
            board[7]["e".toColumnNumber()].name,
        )
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
