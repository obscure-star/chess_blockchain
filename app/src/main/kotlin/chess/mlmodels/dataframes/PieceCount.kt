package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class PieceCount(
    val blackRook: Int,
    val blackKnight: Int,
    val blackBishop: Int,
    val blackQueen: Int,
    val blackKing: Int,
    val blackPawn: Int,
    val empty: Int,
    val whitePawn: Int,
    val whiteRook: Int,
    val whiteKnight: Int,
    val whiteBishop: Int,
    val whiteQueen: Int,
    val whiteKing: Int,
) {
    fun toList(): List<Int> {
        return listOf(
            this.blackRook, this.blackKnight, this.blackBishop, this.blackQueen,
            this.blackKing, this.blackPawn, this.empty, this.whitePawn, this.whiteRook,
            this.whiteKnight, this.whiteBishop, this.whiteQueen, this.whiteKing,
        )
    }
}
