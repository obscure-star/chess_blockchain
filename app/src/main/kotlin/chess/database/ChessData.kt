package chess.database

data class ChessData(
    val round: Int,
    val boardRepresentation: String,
    val pieceCount: String,
    val legalMoves: String,
    val threatsAndAttacks: String,
    val pieceActivity: String,
    val kingSafety: String,
    val pawnStructure: String,
    val materialBalance: String,
    val centerControl: String,
    val previousMoves: String,
)
