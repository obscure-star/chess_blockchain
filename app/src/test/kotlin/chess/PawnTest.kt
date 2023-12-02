package chess

import chess.common.model.Game
import chess.common.model.players.BlackPlayer
import chess.common.model.players.WhitePlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import kotlin.test.assertEquals

class PawnTest {
    private val originalIn = System.`in`
    private val originalOut = System.out

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
    }

    @Test
    fun `test for white pawn (f2) takes black pawn (e7)`() {
        provideInput("white", "f2-f4", "e7-e5", "f4-e5", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.firstPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.firstPlayer.destinationPiece!!.position

        assertEquals(
            "white_pawn",
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
    fun `test for black pawn (e7) takes white pawn (f2)`() {
        provideInput("white", "f2-f4", "e7-e5", "g2-g3", "e5-f4", "q")

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.secondPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.secondPlayer.destinationPiece!!.position

        assertEquals(
            "black_pawn",
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
    fun `test for black pawn (e7) checks and can't take white king (e1)`() {
        provideInput("white", "f2-f4", "e7-e5", "g2-g3", "e5-f4", "g3-g4", "f4-f3", "h2-h3", "f3-f2", "q")

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
    fun `test promote pawn (f2) to queen (f8)`() {
        provideInput(
            "white",
            "f2-f4",
            "e7-e5",
            "g2-g3",
            "e5-f4",
            "g3-g4",
            "f4-f3",
            "h2-h3",
            "f3-f2",
            "e1-f2",
            "f7-f5",
            "g4-f5",
            "f8-c5",
            "f2-e1",
            "c5-b4",
            "f5-f6",
            "d8-e7",
            "f6-g7",
            "h7-h6",
            "g7-h8",
            "Q",
            "e8-f7",
            "h8-g8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_queen",
            board[0]["g".toColumnNumber()].name,
        )
    }

    @Test
    fun `test promote pawn (f2) to rook (f8)`() {
        provideInput(
            "white",
            "f2-f4",
            "e7-e5",
            "g2-g3",
            "e5-f4",
            "g3-g4",
            "f4-f3",
            "h2-h3",
            "f3-f2",
            "e1-f2",
            "f7-f5",
            "g4-f5",
            "f8-c5",
            "f2-e1",
            "c5-b4",
            "f5-f6",
            "d8-e7",
            "f6-g7",
            "h7-h6",
            "g7-h8",
            "R",
            "e8-f7",
            "h8-g8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_rook",
            board[0]["g".toColumnNumber()].name,
        )
    }

    @Test
    fun `test promote pawn (f2) to knight (f8)`() {
        provideInput(
            "white",
            "f2-f4",
            "e7-e5",
            "g2-g3",
            "e5-f4",
            "g3-g4",
            "f4-f3",
            "h2-h3",
            "f3-f2",
            "e1-f2",
            "f7-f5",
            "g4-f5",
            "f8-c5",
            "f2-e1",
            "c5-b4",
            "f5-f6",
            "d8-e7",
            "f6-g7",
            "h7-h6",
            "g7-h8",
            "N",
            "e8-f8",
            "h8-g6",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_knight",
            board[2]["g".toColumnNumber()].name,
        )
    }

    @Test
    fun `test promote pawn (f2) to bishop (f8)`() {
        provideInput(
            "white",
            "f2-f4",
            "e7-e5",
            "g2-g3",
            "e5-f4",
            "g3-g4",
            "f4-f3",
            "h2-h3",
            "f3-f2",
            "e1-f2",
            "f7-f5",
            "g4-f5",
            "f8-c5",
            "f2-e1",
            "c5-b4",
            "f5-f6",
            "d8-e7",
            "f6-g7",
            "h7-h6",
            "g7-h8",
            "B",
            "e8-f8",
            "h8-g7",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_bishop",
            board[1]["g".toColumnNumber()].name,
        )
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
