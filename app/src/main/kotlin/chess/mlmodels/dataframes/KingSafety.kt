package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class KingSafety(
    val whiteKing: Int,
    val blackKing: Int,
) {
    fun toList(): List<Int> {
        return listOf(this.whiteKing, this.blackKing)
    }
}
