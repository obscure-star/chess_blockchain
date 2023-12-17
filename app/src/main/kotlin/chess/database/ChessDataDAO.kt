package chess.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

class ChessDataDAO(private val connection: Connection) {
    fun insertChessData(chessData: ChessData): Boolean {
        return try {
            val sql =
                "INSERT INTO CHESS_DATA (Round, Board_representation, Piece_count, " +
                    "Legal_moves, Threats_and_attacks, Piece_activity, King_safety, " +
                    "Pawn_structure, Material_balance, Center_control, Previous_moves) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)

            preparedStatement.setInt(1, chessData.round)
            preparedStatement.setString(2, chessData.boardRepresentation)
            preparedStatement.setString(3, chessData.pieceCount)
            preparedStatement.setString(4, chessData.legalMoves)
            preparedStatement.setString(5, chessData.threatsAndAttacks)
            preparedStatement.setString(6, chessData.pieceActivity)
            preparedStatement.setString(7, chessData.kingSafety)
            preparedStatement.setString(8, chessData.pawnStructure)
            preparedStatement.setString(9, chessData.materialBalance)
            preparedStatement.setString(10, chessData.centerControl)
            preparedStatement.setString(11, chessData.previousMoves)

            val rowsAffected = preparedStatement.executeUpdate()

            rowsAffected > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }
}
