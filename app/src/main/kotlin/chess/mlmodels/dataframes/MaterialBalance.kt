package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class MaterialBalance(
    val white: Double,
    val black: Double,
) {
    fun toList(): List<Double> {
        return listOf(this.white, this.black)
    }
}
