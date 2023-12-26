package chess.database

import chess.fancyPrintln
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.UUID

class ChessDataDAO(private val connection: Connection) {
    fun insertChessData(chessData: ChessData): Boolean {
        return try {
            val roundId = UUID.randomUUID()
            val sql =
                """
                INSERT INTO CHESS_DATA (
                    ROUND_ID,
                    GAME_ID,
                    ROUND,
                    BOARD_REPRESENTATION,
                    BOARD_REPRESENTATION_INT,
                    PIECE_COUNT,
                    LEGAL_MOVES,
                    THREATS_AND_ATTACKS,
                    PIECE_ACTIVITY,
                    KING_SAFETY,
                    PAWN_STRUCTURE,
                    MATERIAL_BALANCE,
                    CENTER_CONTROL,
                    PREVIOUS_MOVES,
                    MOVE,
                    BLACK_WINS,
                    WHITE_WINS,
                    WINNER,
                    NEXT_MOVE
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

            preparedStatement.setObject(1, roundId.toString())
            preparedStatement.setObject(2, chessData.gameId)
            preparedStatement.setInt(3, chessData.round)
            preparedStatement.setString(4, chessData.boardRepresentation)
            preparedStatement.setInt(5, chessData.boardRepresentationInt)
            preparedStatement.setString(6, Json.encodeToString(chessData.pieceCount))
            preparedStatement.setString(7, Json.encodeToString(chessData.legalMoves))
            preparedStatement.setString(8, Json.encodeToString(chessData.threatsAndAttacks))
            preparedStatement.setString(9, Json.encodeToString(chessData.pieceActivity))
            preparedStatement.setString(10, Json.encodeToString(chessData.kingSafety))
            preparedStatement.setString(11, Json.encodeToString(chessData.pawnStructure))
            preparedStatement.setString(12, Json.encodeToString(chessData.materialBalance))
            preparedStatement.setString(13, Json.encodeToString(chessData.centerControl))
            preparedStatement.setString(14, Json.encodeToString(chessData.previousMoves))
            preparedStatement.setString(15, chessData.move)
            preparedStatement.setBoolean(16, chessData.blackWin)
            preparedStatement.setBoolean(17, chessData.whiteWin)
            preparedStatement.setInt(18, chessData.winner)
            preparedStatement.setString(19, chessData.nextMove)

            val rowsAffected = preparedStatement.executeUpdate()

            fancyPrintln("Record sent to CHESS_DATA database for $roundId and round ${chessData.round}")

            rowsAffected > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
