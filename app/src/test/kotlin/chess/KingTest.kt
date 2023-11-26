package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class KingTest {
    private val originalIn = System.`in`
    private val originalOut = System.out

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `white king (e1) takes black knight (f2)`() {
        provideInput("white", "e2-e4", "g8-f6", "c2-c4", "f6-e4", "d1-a4", "e4-f2", "e1-f2", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.firstPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.firstPlayer.destinationPiece!!.position

        assertEquals(
            "white_king",
            board[
                8 - pieceFinalPosition.row,
            ][pieceFinalPosition.column.toColumnNumber()].name,
        )
        assertEquals(
            "empty",
            board[8 - pieceInitialPosition.row][pieceInitialPosition.column.toColumnNumber()].name,
        )
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
