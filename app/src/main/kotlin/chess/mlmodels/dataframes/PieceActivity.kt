package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class PieceActivity(
    val blackRook: Double,
    val blackKnight: Double,
    val blackBishop: Double,
    val blackQueen: Double,
    val blackKing: Double,
    val blackPawn: Double,
    val empty: Double,
    val whitePawn: Double,
    val whiteRook: Double,
    val whiteKnight: Double,
    val whiteBishop: Double,
    val whiteQueen: Double,
    val whiteKing: Double,
) {
    fun toList(): List<Double> {
        return listOf(
            this.blackRook, this.blackKnight, this.blackBishop, this.blackQueen,
            this.blackKing, this.blackPawn, this.empty, this.whitePawn, this.whiteRook,
            this.whiteKnight, this.whiteBishop, this.whiteQueen, this.whiteKing,
        )
    }
}
