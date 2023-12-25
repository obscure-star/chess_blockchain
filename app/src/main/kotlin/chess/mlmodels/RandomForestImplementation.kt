package chess.mlmodels

import chess.mlmodels.dataframes.DataProcessing
import java.sql.Connection

class RandomForestImplementation {
    private val dataProcessing = DataProcessing()

    fun implementation(connection: Connection) {
        // Data Processing
        val data = connection.createStatement().executeQuery("SELECT * FROM Chess_database.CHESS_DATA")
        val features = mutableListOf<List<*>>()
        val labels = mutableListOf<String>()

        while (data.next()) {
            val boardRepresentation = data.getString("BOARD_REPRESENTATION").toDouble()
            val pieceCount = dataProcessing.convertJsonToPieceCount(data.getString("PIECE_COUNT")).toList()
            val pieceActivity = dataProcessing.convertJsonToPieceActivity(data.getString("PIECE_ACTIVITY")).toList()
            val kingSafety = dataProcessing.convertJsonToKingSafety(data.getString("KING_SAFETY")).toList()
            val materialBalance = dataProcessing.convertJsonToMaterialBalance(data.getString("MATERIAL_BALANCE")).toList()
            val centerControl = dataProcessing.convertJsonToCenterControl(data.getString("CENTER_CONTROL")).toList()
            val nextMove = data.getString("NEXT_MOVE")

            val featureRow = listOf(boardRepresentation) + pieceCount + pieceActivity + kingSafety + materialBalance + centerControl
            features.add(featureRow)
            if (nextMove.length > 1) {
                labels.add(nextMove.substring(1, nextMove.length - 1))
            } else {
                labels.add("END")
            }
        }
    }

    fun splitData(
        features: List<Number>,
        labels: List<Number>,
    ) {
    }
}
