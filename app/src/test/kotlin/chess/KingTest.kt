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

    @Test
    fun `castle move`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "c1-g5",
            "h7-h6",
            "b1-c3",
            "b8-c6",
            "d1-d2",
            "g8-f6",
            "e1-c1",
            "d8-d6",
            "d1-e1",
            "q",
        )

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
    fun `(c1) castle move prevented with white queen between king and rook`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "c1-g5",
            "h7-h6",
            "b1-c3",
            "b8-c6",
            "e1-c1",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_queen",
            board[7][3].name,
        )
    }

    @Test
    fun `(c1) castle move prevented with white queen between king and rook then white king castle when white queen moved`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "c1-g5",
            "h7-h6",
            "b1-c3",
            "b8-c6",
            "e1-c1",
            "d1-d2",
            "c6-d4",
            "e1-c1",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_king",
            board[7][2].name,
        )
        assertEquals(
            "white_rook",
            board[7][3].name,
        )
    }

    @Test
    fun `(g1) castle move prevented with white bishop between king and rook`() {
        provideInput(
            "white",
            "e2-e4",
            "e7-e5",
            "g1-f3",
            "g8-f6",
            "e1-g1",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_bishop",
            board[7][5].name,
        )
    }

    @Test
    fun `(g1) castle move prevented with white bishop between king and rook then white king castle when bishop moved`() {
        provideInput(
            "white",
            "e2-e4",
            "e7-e5",
            "g1-f3",
            "g8-f6",
            "e1-g1",
            "f1-c4",
            "f6-e4",
            "e1-g1",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_rook",
            board[7][5].name,
        )
        assertEquals(
            "white_king",
            board[7][6].name,
        )
    }

    @Test
    fun `(c8) castle move prevented with black knight between king and rook`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "g1-f3",
            "c8-f5",
            "c1-f4",
            "d8-d7",
            "d1-d2",
            "e8-c8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_knight",
            board[0][1].name,
        )
    }

    @Test
    fun `(c8) castle move prevented with black knight between king and rook then king castle when knight moved`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "g1-f3",
            "c8-f5",
            "c1-f4",
            "d8-d7",
            "d1-d2",
            "e8-c8",
            "b8-c6",
            "b8-c6",
            "e2-e4",
            "e8-c8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_rook",
            board[0][3].name,
        )
        assertEquals(
            "black_king",
            board[0][2].name,
        )
    }

    @Test
    fun `(g8) castle move prevented with black bishop between king and rook`() {
        provideInput(
            "white",
            "d2-d4",
            "e7-e5",
            "g1-f3",
            "g8-f6",
            "e2-e4",
            "e8-g8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_bishop",
            board[0][5].name,
        )
    }

    @Test
    fun `(g8) castle move prevented with black bishop between king and rook then king castle when bishop moved`() {
        provideInput(
            "white",
            "d2-d4",
            "e7-e5",
            "g1-f3",
            "g8-f6",
            "e2-e4",
            "e8-g8",
            "f8-b4",
            "c2-c3",
            "e8-g8",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_rook",
            board[0][5].name,
        )
        assertEquals(
            "black_king",
            board[0][6].name,
        )
    }

    @Test
    fun `not checkmate when king (e8) has no moves`() {
        provideInput(
            "white",
            "e2-e4",
            "f7-f5",
            "d1-h5",
            "g7-g6",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        Assertions.assertTrue(game?.firstPlayer is WhitePlayer)
        Assertions.assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
