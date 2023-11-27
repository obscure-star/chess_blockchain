package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RookTest {
    private val originalIn = System.`in`
    private val originalOut = System.out

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `white rook (h2) takes black pawn (h6)`() {
        provideInput("white", "h2-h4", "g7-g5", "h4-g5", "h7-h6", "h1-h6", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.firstPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.firstPlayer.destinationPiece!!.position

        assertEquals(
            "white_rook",
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
    fun `white rook (h2) can't go passed black pawn (h6)`() {
        provideInput("white", "h2-h4", "g7-g5", "h4-g5", "h7-h6", "h1-h8", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        assertNull(game?.firstPlayer?.destinationPiece)
    }

    @Test
    fun `white rook (d1) can't take black king (e8)`() {
        provideInput("white", "h2-h4", "g7-g5", "h4-g5", "h7-h6", "h1-h6", "e7-e6", "h6-e6", "e8-e7", "e6-e7", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_king",
            board[0]["e".toColumnNumber()].name,
        )
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
