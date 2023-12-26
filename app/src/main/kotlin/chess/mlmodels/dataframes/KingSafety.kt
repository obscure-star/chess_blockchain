package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class KingSafety(
    val whiteKing: Double,
    val blackKing: Double,
) {
    fun toList(): List<Double> {
        return listOf(this.whiteKing, this.blackKing)
    }
}
