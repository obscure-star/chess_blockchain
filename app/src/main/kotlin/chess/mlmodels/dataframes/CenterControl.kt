package chess.mlmodels.dataframes

import kotlinx.serialization.Serializable

@Serializable
data class CenterControl(
    val white: Int,
    val black: Int,
) {
    fun toList(): List<Int> {
        return listOf(this.white, this.black)
    }
}
