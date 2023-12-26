package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class CenterControl(
    val white: Double,
    val black: Double,
) {
    fun toList(): List<Double> {
        return listOf(this.white, this.black)
    }
}
