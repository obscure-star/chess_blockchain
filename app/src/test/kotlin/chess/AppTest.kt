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
    fun `white checkmates in 4 moves`() {
        provideInput("white", "e2-e4", "f7-f5", "e4-f5", "g7-g5", "d1-h5", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        game?.firstPlayer?.let { assertTrue(it.winner) }
    }

    @Test
    fun `black checkmates in 3 moves`() {
        provideInput("white d", "f2-f4", "e7-e6", "g2-g4", "d8-h4", "q", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        game?.secondPlayer?.let { assertTrue(it.winner) }
    }

    @Test
    fun `black checkmates in 2 moves`() {
        provideInput("white", "d2-d3", "f7-f6", "e2-e4", "g7-g5", "d1-h5", "q", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        game?.firstPlayer?.let { assertTrue(it.winner) }
    }

    @Test
    fun `the queen's gambit`() {
        provideInput("white", "d2-d4", "d7-d5", "c2-c4", "d5-c4", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `the italian defense (starter move)`() {
        provideInput("white", "e2-e4", "e7-e5", "g1-f3", "b8-c6", "f1-c4", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `the sicilian defense (starter move)`() {
        provideInput("white", "e2-e4", "c7-c5", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `the french defense (starter move)`() {
        provideInput("white", "e2-e4", "e7-e6", "d2-d4", "d7-d5", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `the Ruy-Lopez (starter move)`() {
        provideInput("white", "e2-e4", "e7-e5", "g1-f3", "b8-c6", "f1-b5", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `the slav defense (starter move)`() {
        provideInput("white", "d2-d4", "d7-d5", "c2-c4", "c7-c6", "q")

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

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
    fun `Kasparov (white) vs Topalov (black), Wijk aan Zee 1999 game play test`() {
        provideInput(
            "white d",
            "e2-e4",
            "d7-d6",
            "d2-d4",
            "g8-f6",
            "b1-c3",
            "g7-g6",
            "c1-e3",
            "f8-g7",
            "d1-d2",
            "c7-c6",
            "f2-f3",
            "b7-b5",
            "g1-e2",
            "b8-d7",
            "e3-h6",
            "g7-h6",
            "d2-h6",
            "c8-b7",
            "a2-a3",
            "e7-e5",
            "e1-c1",
            "d8-e7",
            "c1-b1",
            "a7-a6",
            "e2-c1",
            "e8-c8",
            "c1-b3",
            "e5-d4",
            "d1-d4",
            "c6-c5",
            "d4-d1",
            "d7-b6",
            "g2-g3",
            "c8-b8",
            "b3-a5",
            "b7-a8",
            "f1-h3",
            "d6-d5",
            "h6-f4",
            "b8-a7",
            "h1-e1",
            "d5-d4",
            "c3-d5",
            "b6-d5",
            "e4-d5",
            "e7-d6",
            "d1-d4",
            "c5-d4",
            "e1-e7",
            "a7-b6",
            "f4-d4",
            "b6-a5",
            "b2-b4",
            "a5-a4",
            "d4-c3",
            "d6-d5",
            "e7-a7",
            "a8-b7",
            "a7-b7",
            "d5-c4",
            "c3-f6",
            "a4-a3",
            "f6-a6",
            "a3-b4",
            "c2-c3",
            "b4-c3",
            "a6-a1",
            "c3-d2",
            "a1-b2",
            "d2-d1",
            "h3-f1",
            "d8-d2",
            "b7-d7",
            "d2-d7",
            "f1-c4",
            "b5-c4",
            "b2-h8",
            "d7-d3",
            "h8-a8",
            "c4-c3",
            "a8-a4",
            "d1-e1",
            "f3-f4",
            "f7-f5",
            "b1-c1",
            "d3-d2",
            "a4-a7",
            "q",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board
        val pieceFinalPosition = game.firstPlayer.selectedPiece!!.position
        val pieceInitialPosition = game.firstPlayer.destinationPiece!!.position

        assertEquals(
            "white_queen",
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
    fun `Morphy (white) vs Allies (black), Paris Opera 1858 game play test (white wins)`() {
        provideInput(
            "white",
            "e2-e4",
            "e7-e5",
            "g1-f3",
            "d7-d6",
            "d2-d4",
            "c8-g4",
            "d4-e5",
            "g4-f3",
            "d1-f3",
            "d6-e5",
            "f1-c4",
            "g8-f6",
            "f3-b3",
            "d8-e7",
            "b1-c3",
            "c7-c6",
            "c1-g5",
            "b7-b5",
            "c3-b5",
            "c6-b5",
            "c4-b5",
            "b8-d7",
            "e1-c1",
            "a8-d8",
            "d1-d7",
            "d8-d7",
            "h1-d1",
            "e7-e6",
            "b5-d7",
            "f6-d7",
            "b3-b8",
            "d7-b8",
            "d1-d8",
            "q",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "black_king",
            board[0][4].name,
        )
        assertEquals(
            "white_rook",
            board[0][3].name,
        )

        assertTrue(game.firstPlayer.winner)
    }

    @Test
    fun `Aronian (white) vs Anand (black), Wijk aan Zee 2013`() {
        provideInput(
            "white",
            "d2-d4",
            "d7-d5",
            "c2-c4",
            "c7-c6",
            "g1-f3",
            "g8-f6",
            "b1-c3",
            "e7-e6",
            "e2-e3",
            "b8-d7",
            "f1-d3",
            "d5-c4",
            "d3-c4",
            "b7-b5",
            "c4-d3",
            "f8-d6",
            "e1-g1",
            "e8-g8",
            "d1-c2",
            "c8-b7",
            "a2-a3",
            "a8-c8",
            "f3-g5",
            "c6-c5",
            "g5-h7",
            "f6-g4",
            "f2-f4",
            "c5-d4",
            "e3-d4",
            "d6-c5",
            "d3-e2",
            "d7-e5",
            "e2-g4",
            "c5-d4",
            "g1-h1",
            "e5-g4",
            "h7-f8",
            "f7-f5",
            "f8-g6",
            "d8-f6",
            "h2-h3",
            "f6-g6",
            "c2-e2",
            "g6-h5",
            "e2-d3",
            "d4-e3",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_king",
            board[7][7].name,
        )
    }

    @Test
    fun `Byrne (white) vs Fisher (black), New York 1956 (black wins)`() {
        provideInput(
            "white",
            "g1-f3",
            "g8-f6",
            "c2-c4",
            "g7-g6",
            "b1-c3",
            "f8-g7",
            "d2-d4",
            "e8-g8",
            "c1-f4",
            "d7-d5",
            "d1-b3",
            "d5-c4",
            "b3-c4",
            "c7-c6",
            "e2-e4",
            "b8-d7",
            "a1-d1",
            "d7-b6",
            "c4-c5",
            "c8-g4",
            "f4-g5",
            "b6-a4",
            "c5-a3",
            "a4-c3",
            "b2-c3",
            "f6-e4",
            "g5-e7",
            "d8-b6",
            "f1-c4",
            "e4-c3",
            "e7-c5",
            "f8-e8",
            "e1-f1",
            "g4-e6",
            "c5-b6",
            "e6-c4",
            "f1-g1",
            "c3-e2",
            "g1-f1",
            "e2-d4",
            "f1-g1",
            "d4-e2",
            "g1-f1",
            "e2-c3",
            "f1-g1",
            "a7-b6",
            "a3-b4",
            "a8-a4",
            "b4-b6",
            "c3-d1",
            "h2-h3",
            "a4-a2",
            "g1-h2",
            "d1-f2",
            "h1-e1",
            "e8-e1",
            "b6-d8",
            "g7-f8",
            "f3-e1",
            "c4-d5",
            "e1-f3",
            "f2-e4",
            "d8-b8",
            "b7-b5",
            "h3-h4",
            "h7-h5",
            "f3-e5",
            "g8-g7",
            "h2-g1",
            "f8-c5",
            "g1-f1",
            "e4-g3",
            "f1-e1",
            "c5-b4",
            "e1-d1",
            "d5-b3",
            "d1-c1",
            "g3-e2",
            "c1-b1",
            "e2-c3",
            "b1-c1",
            "a2-c2",
            "q",
        )

        main()

        val game = Game.getCurrentGame()
        assertTrue(game?.firstPlayer is WhitePlayer)
        assertTrue(game?.secondPlayer is BlackPlayer)

        val board = game!!.board.board

        assertEquals(
            "white_king",
            board[7][2].name,
        )

        assertTrue(game.secondPlayer.winner)
    }

    private fun provideInput(vararg inputs: String) {
        System.setIn(ByteArrayInputStream(inputs.joinToString("\n").toByteArray()))
    }
}
