package chess.database

import kotlinx.serialization.Serializable

@Serializable
data class ChessData(
    val gameId: String,
    val round: Int,
    val boardRepresentation: String,
    val boardRepresentationInt: Int,
    val pieceCount: String,
    val legalMoves: String,
    val threatsAndAttacks: String,
    val pieceActivity: String,
    val kingSafety: String,
    val pawnStructure: String?,
    val materialBalance: String,
    val centerControl: String,
    val previousMoves: String,
    val move: String,
    val blackWin: Boolean,
    val whiteWin: Boolean,
    val winner: Int,
)
