package chess.database

import chess.fancyPrintln
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class ChessDataDAO(private val connection: Connection) {
    fun insertChessData(chessData: ChessData): Boolean {
        return try {
            val sql =
                """
                INSERT INTO CHESS_DATA (
                    GAME_ID,
                    ROUND,
                    BOARD_REPRESENTATION,
                    BOARD_REPRESENTATION_INT,
                    PIECE_COUNT,
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
                    NEXT_MOVE,
                    NEXT_MOVE_INDEX,
                    LENGTH_LEGAL_MOVES,
                    LEGAL_MOVES
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()

            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

            preparedStatement.setObject(1, chessData.gameId)
            preparedStatement.setInt(2, chessData.round)
            preparedStatement.setString(3, chessData.boardRepresentation)
            preparedStatement.setInt(4, chessData.boardRepresentationInt)
            preparedStatement.setString(5, Json.encodeToString(chessData.pieceCount))
            preparedStatement.setString(6, Json.encodeToString(chessData.threatsAndAttacks))
            preparedStatement.setString(7, Json.encodeToString(chessData.pieceActivity))
            preparedStatement.setString(8, Json.encodeToString(chessData.kingSafety))
            preparedStatement.setString(9, Json.encodeToString(chessData.pawnStructure))
            preparedStatement.setString(10, Json.encodeToString(chessData.materialBalance))
            preparedStatement.setString(11, Json.encodeToString(chessData.centerControl))
            preparedStatement.setString(12, Json.encodeToString(chessData.previousMoves))
            preparedStatement.setString(13, chessData.move)
            preparedStatement.setBoolean(14, chessData.blackWin)
            preparedStatement.setBoolean(15, chessData.whiteWin)
            preparedStatement.setInt(16, chessData.winner)
            preparedStatement.setString(17, chessData.nextMove)
            preparedStatement.setInt(18, chessData.nextMoveIndex)
            preparedStatement.setInt(19, chessData.lengthLegalMoves)
            preparedStatement.setString(20, Json.encodeToString(chessData.legalMoves))

            val rowsAffected = preparedStatement.executeUpdate()

            fancyPrintln("Record sent to CHESS_DATA database for ${chessData.gameId} and round ${chessData.round}")

            rowsAffected > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
